package org.grs.generator.model;

import org.grs.generator.common.IService;

/**
 * ModuleService
 *
 * @author f0rb on 2017-01-21.
 */
public interface ModuleService extends IService<Module> {

    Module importModule(Module module);

    Module importModule(Integer projectId, String createSql);
}
