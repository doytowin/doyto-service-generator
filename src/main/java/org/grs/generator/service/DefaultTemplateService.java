package org.grs.generator.service;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.grs.generator.common.AbstractService;
import org.grs.generator.component.mybatis.IMapper;
import org.grs.generator.mapper.TemplateMapper;
import org.grs.generator.model.Template;

/**
 * DefaultModulService
 *
 * @author f0rb on 2017-01-21.
 */
@Slf4j
@Service
public class DefaultTemplateService extends AbstractService<Template> implements TemplateService {
    @Resource
    private TemplateMapper templateMapper;

    @Override
    public IMapper<Template> getIMapper() {
        return templateMapper;
    }

    @Override
    public Template update(Template template) {
        Template target = templateMapper.get(template.getId());
        if (target == null) {
            return null;
        }

        target.setSuffix(template.getSuffix());
        target.setPath(template.getPath());
        target.setCap(template.getCap());
        target.setContent(template.getContent());
        target.setValid(template.getValid());
        templateMapper.update(target);
        return target;
    }
}
