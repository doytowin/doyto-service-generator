package win.doyto.service.generator.module.template;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import win.doyto.query.web.controller.AbstractIQEEController;

/**
 * 模板管理模块基本操作。
 *
 * @author Yuanzhen on 2016-07-22.
 */
@Slf4j
@RestController
@RequestMapping("/api/template")
public class TemplateController extends AbstractIQEEController<TemplateEntity, Integer, TemplateQuery> {

}