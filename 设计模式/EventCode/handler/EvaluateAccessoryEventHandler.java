package com.yf.ShoppingCart.web.event.handler;

import com.yf.ShoppingCart.web.constants.CRMChangeType;
import com.yf.ShoppingCart.web.constants.MemberTaskTypeEnum;
import com.yf.ShoppingCart.web.event.dto.MemberTaskDTO;
import com.yf.ShoppingCart.web.event.member.EvaluateAccessoryEvent;
import com.yf.ShoppingCart.web.util.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


/**
 * 商品评价（配件or小件）事件处理
 */
@Component
@Slf4j
public class EvaluateAccessoryEventHandler extends AbstractEventHandler<EvaluateAccessoryEvent> {

    @Override
    public void handleEvent(EvaluateAccessoryEvent event) {
        String token = event.getToken();
        String userCenterUserId = tkService.getUserCenterUserId(token);
        if (StringUtils.isBlank(userCenterUserId)) {
            log.error("EvaluateAccessoryEventHandler Failure, userCenterUserId Don't Exist, token :{}", token);
            return;
        }

        MemberTaskDTO memberTaskDTO = new MemberTaskDTO();
        memberTaskDTO.setUserId(userCenterUserId);
        memberTaskDTO.setOrderId(event.getOrderId());
        memberTaskDTO.setChangeType(CRMChangeType.ADD.code());
        memberTaskDTO.setTaskType(MemberTaskTypeEnum.EVALUATE_ACCESSORY.code());
        tkService.sendMsgToCRM(memberTaskDTO);
    }

}
