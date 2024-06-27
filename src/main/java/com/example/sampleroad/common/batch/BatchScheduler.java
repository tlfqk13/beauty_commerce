package com.example.sampleroad.common.batch;

import com.example.sampleroad.service.AuthenticationsService;
import com.example.sampleroad.service.PopUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchScheduler {

    private final AuthenticationsService authenticationsService;
    private final PopUpService popUpService;
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteAuthenticationSendCount(){
        authenticationsService.deleteAuthenticationSendCount();
    }

    /**
     * 매일 자정, 새벽 1시, 새벽 2시, 새벽 3시, 새벽 4시에 총 5번 테이블에서 데이터를 삭제하는 작업을 정기적으로 실행
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/12/18
     **/
    @Scheduled(cron = "0 0 0-4 * * *")
    public void deletePopupMemberVisible(){
        popUpService.deletePopupMemberVisible();
    }


    /**
     * 주문하고 7일 지났으면 리뷰 유도 푸시
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2024/01/09
     **/

}
