package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.domain.grouppurchase.GroupPurchaseType;
import com.example.sampleroad.dto.request.order.OrderRequestDto;
import com.example.sampleroad.dto.response.grouppurchase.GroupPurchaseResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.GroupPurchaseOrderService;
import com.example.sampleroad.service.GroupPurchaseService;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = {"팀구매 관련 api Controller"})
@RequiredArgsConstructor
public class GroupPurchaseController {

    private final GroupPurchaseService groupPurchaseService;
    private final GroupPurchaseOrderService groupPurchaseOrderService;

    @GetMapping("/api/group-purchase/room/{productNo}")
    @ApiOperation(value = "공동구매 방 리스트 조회하기")
    public void getGroupPurchaseRooms(@PathVariable int productNo) {
        groupPurchaseService.getGroupPurchaseRooms(productNo);
    }

    @PostMapping("/api/group-purchase/room/make/{productNo}")
    @ApiOperation(value = "공동구매 방 만들기")
    public ResultInfo makeGroupPurchaseRoom(@PathVariable int productNo,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // TODO: 2/6/24 N인 팀구매 열기
        groupPurchaseService.makeGroupPurchaseRoom(productNo, userDetails, GroupPurchaseType.ROOM_MAKE);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "공동구매 방 만들기 완료");
    }

    // TODO: 2/6/24 방참여
    @PostMapping("/api/group-purchase/room/join/{roomId}")
    @ApiOperation(value = "공동구매 방 참여하기")
    public ResultInfo joinGroupPurchaseRoom(@PathVariable Long roomId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // TODO: 2/6/24 참여하기
        groupPurchaseService.joinGroupPurchaseRoom(roomId, userDetails, GroupPurchaseType.ROOM_JOIN);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "공동구매 방 참여하기 완료");
    }

    /**
     * 공동구매 주문하기 요청
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2/6/24
     **/
    @PostMapping("/api/group-purchase/order")
    @ApiOperation(value = "공동구매를 위한 주문서 작성하기")
    public ResultInfo createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                  @RequestBody OrderRequestDto.GroupPurchaseOrder dto) throws UnirestException, ParseException {
        GroupPurchaseResponseDto.CreateOrderSheet order = groupPurchaseOrderService.createOrder(userDetails, dto);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "주문서 작성 완료", order);
    }

    @GetMapping("/api/group-purchase")
    @ApiOperation(value = "팀구매 상품 리스트 조회")
    public List<GroupPurchaseResponseDto.SectionInfo> getGroupPurchaseProductList() throws UnirestException, ParseException {
        return groupPurchaseService.getGroupPurchaseSection();
    }

    @GetMapping("/api/group-purchase/info")
    @ApiOperation(value = "팀구매 현황")
    public GroupPurchaseResponseDto.PurchaseInfo getGroupPurchasePurchaseInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        return groupPurchaseOrderService.getGroupPurchasePurchaseInfo(userDetails);
    }
}
