package com.hcltech.ppmtool.services;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcltech.ppmtool.domain.Backlog;
import com.hcltech.ppmtool.domain.Project;
import com.hcltech.ppmtool.domain.User;
import com.hcltech.ppmtool.exceptions.ProjectIdException;
import com.hcltech.ppmtool.exceptions.ProjectNotFoundException;
import com.hcltech.ppmtool.repositories.BacklogRepository;
import com.hcltech.ppmtool.repositories.ProjectRepository;
import com.hcltech.ppmtool.repositories.UserRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private BacklogRepository backlogRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public Project saveOrUpdateProject(Project project, String username) {
		 
		if(project.getId() != null) {
			Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());
			
			if(existingProject != null && !existingProject.getProjectLeader().equals(username)) {
				throw new ProjectNotFoundException("Project not found in your account");
			} 
			else if (existingProject == null) {
				throw new ProjectNotFoundException("Project with ID: '"
													+ project.getProjectIdentifier()
													+ "' cannot be updated because it doesn't exist");
			}
		} 
		
		try {
			
			User user = userRepository.findByUsername(username);
			
			project.setUser(user);
			project.setProjectLeader(user.getUsername());
			
			project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			
		
			if(project.getId() == null) {
				Backlog backlog = new Backlog();
				project.setBacklog(backlog);
				backlog.setProject(project);
				backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			}
			
			if(project.getId() != null) {
				project.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier().toUpperCase()));
			}
			
			return projectRepository.save(project);
		} catch (Exception e) {
			throw new ProjectIdException(("Project ID '" 
					+ project.getProjectIdentifier().toUpperCase()) 
					+ "' already exists");
		}
		
	} // saveOrUpdateProject
	
	public Project findProjectByIdentifier(String projectId, String username) {
		
		Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
		
		if (project == null) {
			throw new ProjectIdException("Project ID '" 
					+ projectId 
					+ "' does not exist");
		} // if
		
		if(!project.getProjectLeader().equals(username)) {
			throw new ProjectNotFoundException("Project not found in your account");
		} // if
		
		return project;
	} // findProjectByIdentifier
	
	public Iterable<Project> findAllProjects(String username){
		return projectRepository.findAllByProjectLeader(username);
	} // findAllProjects
	
	public void deleteProjectByIdentifier(String projectId, String username) {		
		projectRepository.delete(findProjectByIdentifier(projectId, username));
	} // deleteProjectByIdentifier
	
} // class
