/*
 * The MIT License
 *
 * Copyright 2012 pgmillon.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.ishtanzar.java.maven;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.drupal.ModuleInfoParser;

/**
 * Goal which touches a timestamp file.
 *
 * @goal check-dependencies
 *
 * @phase process-resources
 */
public class CheckDependenciesMojo extends AbstractMojo {

  /**
   * Location of the file. 
   * @parameter default-value="${basedir}/src/main/php/${module.name}.info"
   * @required
   */
  private File moduleInfoFile;
  
  /**
   * @parameter default-value="${project}"
   */
  private MavenProject mavenProject;
  
  public void execute() throws MojoExecutionException {
    ModuleInfoParser parser = new ModuleInfoParser();
    
    try {
      parser.read(moduleInfoFile);
    } catch (IOException ex) {
      throw new MojoExecutionException("An error occured while trying to check dependencies", ex);
    }
    
    List<Dependency> mavenDependencies = mavenProject.getDependencies();
    Set<String> missingDependencies = new HashSet<String>();
    Boolean hasMatch;
    
    for(String drupalDependency : parser.getDependencies()) {
      hasMatch = false;
      for(Dependency mavenDependency : mavenDependencies) {
        if(mavenDependency.getArtifactId().equals(drupalDependency)) {
          hasMatch = true;
          break;
        }
      }
      if(!hasMatch) {
        missingDependencies.add(drupalDependency);
      }
    }
    
    if(!missingDependencies.isEmpty()) {
      throw new MojoExecutionException("There are unmet dependencies: " + Arrays.toString(missingDependencies.toArray()));
    }
  }
}
