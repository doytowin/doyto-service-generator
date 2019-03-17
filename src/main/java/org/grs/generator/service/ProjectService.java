package org.grs.generator.service;

import org.grs.generator.common.IService;
import org.grs.generator.model.Project;

/**
 * ModuleService
 *
 * @author f0rb on 2017-01-21.
 */
public interface ProjectService extends IService<Project> {

    void importDatabase(Integer projectId);

}
