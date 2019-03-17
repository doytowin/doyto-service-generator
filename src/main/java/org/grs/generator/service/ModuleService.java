package org.grs.generator.service;

import org.grs.generator.common.IService;
import org.grs.generator.model.Module;
import org.grs.generator.model.Project;

/**
 * ModuleService
 *
 * @author f0rb on 2017-01-21.
 */
public interface ModuleService extends IService<Module> {

    Module importModule(Module module);

    Module importModule(Integer projectId, String createSql);

    Module importModule(Integer projectId, String table, String createSql);

    Module importModule(Project project, String table, String createSql);
}
