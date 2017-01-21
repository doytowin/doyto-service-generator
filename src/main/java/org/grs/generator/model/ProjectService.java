package org.grs.generator.model;

import org.grs.generator.common.IService;

/**
 * ModuleService
 *
 * @author f0rb on 2017-01-21.
 */
public interface ProjectService extends IService<Project> {

    Project importDatabase(Integer projectId);

}
