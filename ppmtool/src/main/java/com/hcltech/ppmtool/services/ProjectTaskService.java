package com.hcltech.ppmtool.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcltech.ppmtool.domain.Backlog;
import com.hcltech.ppmtool.domain.Project;
import com.hcltech.ppmtool.domain.ProjectTask;
import com.hcltech.ppmtool.exceptions.ProjectNotFoundException;
import com.hcltech.ppmtool.repositories.BacklogRepository;
import com.hcltech.ppmtool.repositories.ProjectRepository;
import com.hcltech.ppmtool.repositories.ProjectTaskRepository;

@Service
public class ProjectTaskService {

	@Autowired
	private BacklogRepository backlogRepository;
	
	@Autowired
	private ProjectTaskRepository projectTaskRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private ProjectService projectService;
	
	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username) {
		
		// Exception: Project not found
		
		
			// PTs to be added to a specific project, project != null, BL exists
			Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, username).getBacklog(); //backlogRepository.findByProjectIdentifier(projectIdentifier);
			
			
			// set the BL to PT 
			projectTask.setBacklog(backlog);
			// we want our project sequence to be like this: IDPRO-1	IDPRO-2 ... IDPRO-n
			Integer backlogSequence = backlog.getPTSequence();
			// update the BL sequence
			backlogSequence++;
			backlog.setPTSequence(backlogSequence);
			
			// add sequence to PT
			projectTask.setProjectSequence(projectIdentifier + "-" + backlogSequence);
			projectTask.setProjectIdentifier(projectIdentifier);
			
			// INITIAL priority when priority null
			// In the future, we need projectTask.getPriority == 0 to handle the form 
			if (projectTask.getPriority() == null || projectTask.getPriority() == 0) {
				projectTask.setPriority(3);
			}
			// INITIAL status when status is null
			if (projectTask.getStatus() == "" || projectTask.getStatus() == null) {
				projectTask.setStatus("TO_DO");
			}
			
			return projectTaskRepository.save(projectTask);
				
	
	} // addProjectTask

	public Iterable<ProjectTask> findBacklogById(String id, String username) {
		
		projectService.findProjectByIdentifier(id, username);
		return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
	} // findBacklogById
	
	public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id, String username) {
				
		// make sure we are  searching on an existing backlog
		projectService.findProjectByIdentifier(backlog_id, username);

		// make sure that our task exists
		ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
		if (projectTask == null) {
			throw new ProjectNotFoundException("Project Task: '" + pt_id + "' not found");
		}
		
		// make sure that the backlog/project id in the path corresponds to the right project
		if (!projectTask.getProjectIdentifier().contentEquals(backlog_id)) {
			throw new ProjectNotFoundException("Project Task: '" + pt_id 
										+ "' does not exist in project: '" + backlog_id + "'" );
		}
		
		return projectTask;
	} // findPTByProjectSequence
	
	public ProjectTask updatePTByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id, String username) {
		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);
		
		projectTask = updatedTask;
		
		return projectTaskRepository.save(projectTask);
	} // updatePTByProjectSequence
	
	public void deletePTByProjectSequence(String backlog_id, String pt_id, String username) {
		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);
		
		projectTaskRepository.delete(projectTask);
	} // deletePTByProjectSequence
	
} // class
