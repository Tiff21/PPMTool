package com.hcltech.ppmtool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcltech.ppmtool.domain.Project;
import com.hcltech.ppmtool.exceptions.ProjectIdException;
import com.hcltech.ppmtool.repositories.ProjectRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;
	
	public Project saveOrUpdateProject(Project project) {
		 
		try {
			project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			return projectRepository.save(project);
		} catch (Exception e) {
			throw new ProjectIdException(("Project ID '" 
					+ project.getProjectIdentifier().toUpperCase()) 
					+ "' already exists");
		}
		
	} // saveOrUpdateProject
	
	public Project findProjectByIdentifier(String projectId) {
		
		Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
		
		if (project == null) {
			throw new ProjectIdException("Project ID '" 
					+ projectId 
					+ "' does not exist");
		} // if
		return project;
	} // findProjectByIdentifier
	
	public Iterable<Project> findAllProjects(){
		return projectRepository.findAll();
	} // findAllProjects
	
	public void deleteProjectByIdentifier(String projectId) {
		Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
		
		if (project == null) {
			throw new ProjectIdException("Connot delete Project with ID '" 
											+ projectId 
											+ "'. This project does not exist");	
		} // if
		
		projectRepository.delete(project);
	} // deleteProjectByIdentifier
	
} // class
