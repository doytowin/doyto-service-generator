package win.doyto.service.generator.module.project;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import win.doyto.query.controller.AbstractSimpleController;

/**
 * 项目管理模块基本操作。
 *
 * @author Yuanzhen on 2016-07-22.
 */
@Slf4j
@RestController
@RequestMapping("/api/project")
public class ProjectController extends AbstractSimpleController<ProjectEntity, Integer, ProjectQuery> implements ProjectApi {

}