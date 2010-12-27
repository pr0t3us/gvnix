/*
 * gvNIX. Spring Roo based RAD tool for Conselleria d'Infraestructures
 * i Transport - Generalitat Valenciana
 * Copyright (C) 2010 CIT - Generalitat Valenciana
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gvnix.dynamic.configuration.roo.addon.config;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.gvnix.dynamic.configuration.roo.addon.entity.ProjectPath;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.process.manager.MutableFile;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;

/**
 * Abstract dynamic configuration component of any file.
 * <p>
 * Extends this class to manage a new file.
 * </p>
 * 
 * @author Mario Martínez Sánchez ( mmartinez at disid dot com ) at <a
 *         href="http://www.disid.com">DiSiD Technologies S.L.</a> made for <a
 *         href="http://www.cit.gva.es">Conselleria d'Infraestructures i
 *         Transport</a>
 */
@Component(componentAbstract = true)
public abstract class FileDynamicConfiguration {
  
  @Reference protected PathResolver pathResolver;
  @Reference protected FileManager fileManager;

  /**
   * Get the dynamic configuration file path.
   * 
   * @return Dynamic configuration file path
   */
  public abstract String getFilePath();

  /**
   * Get the properties mutable file.
   * 
   * @return Properties mutable file
   */
  protected MutableFile getFile() {

    String path = pathResolver.getIdentifier(new Path(ProjectPath.ROOT.name()),
        getFilePath());
    if (fileManager.exists(path)) {

      return fileManager.updateFile(path);
    }
    else {

      throw new IllegalStateException("File not found");
    }
  }

}