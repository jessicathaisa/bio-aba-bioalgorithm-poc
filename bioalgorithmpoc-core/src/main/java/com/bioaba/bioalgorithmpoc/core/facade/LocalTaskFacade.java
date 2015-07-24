package com.bioaba.bioalgorithmpoc.core.facade;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bioaba.bioalgorithmpoc.core.service.LocalTaskService;
import com.bioaba.bioalgorithmpoc.persistence.entity.LocalTask;
import com.bioaba.bioalgorithmpoc.persistence.entity.LocalTaskParameter;

@Component
@Transactional
public class LocalTaskFacade {

	private LocalTaskService taskService;

	@Autowired
	public LocalTaskFacade(LocalTaskService taskService) {
		this.taskService = taskService;
	}

	public LocalTask find(Long taskId) {
		LocalTask task = taskService.find(taskId);
		return task;
	}

	public LocalTask findByBioABATaskID(String taskId) {
		LocalTask task = taskService.findBytaskKeyBioABA(taskId);
		return task;
	}
	
	public LocalTask saveTask(LocalTask entity, byte[] query) {

		entity.setStatus("PROCESSING");
		taskService.save(entity);
		
		String caminho = "";
		try {
			File file = new File("/home/ec2-user/query_" + entity.getTaskKeyBioABA() + ".faa");
			FileOutputStream os = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			String aux = new String(query).substring(10).replace("\\n", "\n");
			aux = aux.substring(0, aux.length() - 2 );
			bos.write(aux.getBytes());
			bos.close();
			os.close();

			caminho = "/home/ec2-user/query_" + entity.getTaskKeyBioABA() + ".faa";

			entity.setQuery(caminho);
		} catch (Exception ex) {
			entity.setStatus("FINISHED WITH ERRORS");	
		}
		taskService.save(entity);

		return entity;
	}
	
	

	public void runTask(Long taskId) {
		LocalTask task = taskService.find(taskId);
		
		try{	
			String cmdBusca = "ls /home/ec2-user";
			String output = executeCommand(cmdBusca);
	
			if (!output.contains(task.getDatabaseName() + ".dmnd")) {
				String cmdCriaBanco = "/home/ec2-user/./diamond makedb --in /home/ec2-user/"
						+ task.getDatabaseName()
						+ " -d /home/ec2-user/"
						+ task.getDatabaseName();
				output = executeCommand(cmdCriaBanco);
				if (!output.equals(""))
					System.out.println("Erro ao criar banco");
			}
	
			String cmdCriaPastaAuxiliar = "mkdir /home/ec2-user/banco" + taskId;
			String cmdExcluiPastaAuxiliar = "rm -r /home/ec2-user/banco" + taskId;
			String cmdRodaAlgoritmo = "/home/ec2-user/./diamond ";
			cmdRodaAlgoritmo += task.getAlgorithmName() + " ";
			cmdRodaAlgoritmo += "-d " + "/home/ec2-user/" + task.getDatabaseName() + " ";
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
	
			task.setStatus("SUCCESSFULLY COMPLETED");
			task.setResult("/home/ec2-user/resultado_" + taskId + ".m8");
			
			taskService.save(task);
		}
		catch (Exception ex){	
			task.setStatus("FINISHED WITH ERRORS");			
			taskService.save(task);
		}
		
		// Sent PUT callback's URL
		putResultToTaskManager(task);

	}
	
	public String findResult(String path){
		String text = "";
		try{
			BufferedReader br = new BufferedReader(new FileReader(path));
			 
			String line = null;
			while ((line = br.readLine()) != null) {
				text += line + "\n";
			}
		 
			br.close();
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		
		return text;
	}

	
	private void putResultToTaskManager(LocalTask task){
		RestTemplate restTemplate = new RestTemplate();
		
		try {
			String URL = task.getCallbackURL() + "/" + task.getTaskKeyBioABA();
			Map<String, Object> parts = new HashMap<String, Object>();
			parts.put("algorithmName", task.getAlgorithmName());
			Map<String, String> map = new HashMap<String, String>();
			String result = Base64.getEncoder().encodeToString((findResult(task.getResult())).getBytes());
			
			map.put("result", result);
			map.put("status", task.getStatus());
			

			restTemplate.put(URL, map);
			
		} catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println("Error while receiving response of algorithm: Wrong Location");
		} catch (Exception ex) {
			System.out.println("Error while receiving response of algorithm: Invalid URL");
		}
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
