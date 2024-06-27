package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.domain.experience.ExperienceStatus;
import com.example.sampleroad.dto.request.ExperienceRequestDto;
import com.example.sampleroad.dto.response.experience.ExperienceResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.ExperiencePartyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"체험단 관련 api Controller"})
public class ExperiencePartyController {

    private final ExperiencePartyService experiencePartyService;

    @GetMapping("/api/experience")
    @ApiOperation(value = "체험단 전체 리스트 조회")
    public ExperienceResponseDto getExperienceList(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @RequestParam(defaultValue = CustomValue.pageNumber) int pageNumber,
                                                   @RequestParam(defaultValue = CustomValue.pageSize) int pageSize,
                                                   @RequestParam String isMyExperience,
                                                   @RequestParam ExperienceStatus experienceStatus) {

        return experiencePartyService.getExperienceList(userDetails, pageNumber, pageSize, isMyExperience, experienceStatus);
    }

    @GetMapping("/api/experience/{experienceId}")
    @ApiOperation(value = "체험단 상세 API")
    public ExperienceResponseDto.ExperienceDetail getExperienceDetail(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                      @PathVariable Long experienceId) {
        return experiencePartyService.getExperienceDetail(userDetails, experienceId);
    }

    @PostMapping("/api/experience")
    @ApiOperation(value = "체험단 신청 API")
    public ResultInfo addExperience(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                    @RequestBody ExperienceRequestDto dto) {
        experiencePartyService.addExperience(userDetails, dto);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "체험단 신청 완료");
    }

    @GetMapping("/api/experience/winner/{experienceId}")
    @ApiOperation(value = "당첨 여부 확인하기 API ")
    public ResultInfo getExperienceWinner(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @PathVariable Long experienceId) {
        HashMap<String, Object> resultInfo = experiencePartyService.getExperienceWinner(userDetails, experienceId);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "당첨 여부 확인", resultInfo);
    }

    /**
     * 신청 확인하기 API
     *
     * @param
     * @return
     * @author Lina
     * @version 1.0.0
     * @date 2023/12/07
     **/
    @GetMapping("/api/experience/register-check/{experienceId}")
    @ApiOperation(value = "체험단 신청 확인하기 API")
    public ExperienceResponseDto.ExperienceRegisterCheck getRegisterCheckExperience(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                    @PathVariable Long experienceId) {
        return experiencePartyService.getRegisterCheckExperience(userDetails, experienceId);
    }
}
