package com.bioaba.bioalgorithmpoc.core.facade;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.bioaba.bioalgorithmpoc.core.events.LocalTaskSavedEvent;
import com.bioaba.bioalgorithmpoc.core.service.LocalTaskService;
import com.bioaba.bioalgorithmpoc.persistence.entity.LocalTask;
import com.bioaba.bioalgorithmpoc.persistence.entity.LocalTaskParameter;

@Component
@Transactional
public class LocalTaskFacade {

	private LocalTaskService taskService;

	@Inject
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	public LocalTaskFacade(LocalTaskService taskService) {
		this.taskService = taskService;
	}

	public LocalTask find(Long taskId) {
		LocalTask task = taskService.find(taskId);
		return task;
	}

	public LocalTask saveTask(LocalTask entity, byte[] query) {
		String caminho = "";
		try {
			File file = new File("/home/ec2-user/query_" + entity.getTaskKeyBioABA());
			FileOutputStream os = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			
			bos.write(query);
			bos.close();
			os.close();

			caminho = "/home/ec2-user/query_" + entity.getTaskKeyBioABA();
		} catch (Exception ex) {
		}

		entity.setQuery(caminho);
		taskService.save(entity);

		eventPublisher.publishEvent(new LocalTaskSavedEvent(this, entity
				.getId()));

		return entity;
	}

	public void runTask(Long taskId) {
		LocalTask task = taskService.find(taskId);

		String cmdBusca = "ls /home/ec2-user";
		String output = executeCommand(cmdBusca);

		if (!output.contains(task.getDatabaseName())) {
			String cmdCriaBanco = "/home/ec2-user/./diamond makedb --in /home/ec2-user/"
					+ task.getDatabaseName()
					+ " -d /home/ec2-user/banco_"
					+ taskId;
			output = executeCommand(cmdCriaBanco);
			if (output.equals(""))
				System.out.println("Erro ao criar banco");
		}

		String cmdCriaPastaAuxiliar = "mkdir /home/ec2-user/banco" + taskId;
		String cmdExcluiPastaAuxiliar = "rm /home/ec2-user/banco" + taskId;
		String cmdRodaAlgoritmo = "/home/ec2-user/./diamond ";
		cmdRodaAlgoritmo += task.getAlgorithmName() + " ";
		cmdRodaAlgoritmo += "-d " + "/home/ec2-user/banco_" + taskId + " ";
		cmdRodaAlgoritmo += "-q " + task.getQuery() + " ";
		for (LocalTaskParameter param : task.getParameters()) {
			String chave = param.getChave();
			String valor = param.getValor();
			if (valor != null && !valor.isEmpty())
				cmdRodaAlgoritmo += chave + " " + valor + " ";
		}
		cmdRodaAlgoritmo += "-a " + "/home/ec2-user/resultado_" + taskId + " ";
		cmdRodaAlgoritmo += "-t " + "/home/ec2-user/banco" + taskId + " ";

		output = executeCommand(cmdCriaPastaAuxiliar);
		output = executeCommand(cmdRodaAlgoritmo);
		output = executeCommand(cmdExcluiPastaAuxiliar);

		String cmdView = "/home/ec2-user/./diamond view -a /home/ec2-user/resultado_"
				+ taskId
				+ ".daa -o /home/ec2-user/resultado_"
				+ taskId
				+ ".m8 -f sam";
		output = executeCommand(cmdView);

		String cmdCat = "cat /home/ec2-user/resultado_" + taskId + ".m8";
		output = executeCommand(cmdCat);

		System.out.println(output);

	}

	private String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}
}
