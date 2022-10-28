package win.doyto.service.generator.common;

import org.springframework.stereotype.Component;
import win.doyto.query.entity.UserIdProvider;

/**
 * EmptyUserIdProvider
 *
 * @author f0rb on 2022/10/28
 * @since 1.0.0
 */
@Component
public class EmptyUserIdProvider implements UserIdProvider<Integer> {
    @Override
    public Integer getUserId() {
        return 0;
    }
}
