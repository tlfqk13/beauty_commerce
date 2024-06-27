package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.domain.Version;
import com.example.sampleroad.dto.request.VersionRequestDto;
import com.example.sampleroad.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VersionService {
    private final VersionRepository versionRepository;

    public void checkVersion(VersionRequestDto requestDto) {
        String os = requestDto.getOs();
        String version = requestDto.getVersion();
        int userVersionNum = convertVersion(version);
        // 최신 버전 및 필수 업데이트 버전 조회
        Optional<Version> requiredVersionOpt = versionRepository.findByOsAndIsRequiredUpdate(os, true);
        Optional<Version> notifyVersionOpt = versionRepository.findByOsAndIsNotifyUpdate(os, true);

        int requiredVersionNum = requiredVersionOpt.map(v -> convertVersion(v.getVersionName())).orElse(0);
        int notifyVersionNum = notifyVersionOpt.map(v -> convertVersion(v.getVersionName())).orElse(0);

        if (userVersionNum < notifyVersionNum && notifyVersionNum < requiredVersionNum) {
            throw new ErrorCustomException(ErrorCode.NECESSARY_VERSION_UPDATE);
        }

        if (requiredVersionNum < userVersionNum && userVersionNum < notifyVersionNum) {
            throw new ErrorCustomException(ErrorCode.UNNECESSARY_VERSION_UPDATE);
        }

        // 먼저 필수 업데이트가 있는지 체크
        if (userVersionNum < requiredVersionNum) {
            throw new ErrorCustomException(ErrorCode.NECESSARY_VERSION_UPDATE);
        }

        // 필수 업데이트가 없으면, 선택적 업데이트가 있는지 체크
        if (userVersionNum < notifyVersionNum) {
            throw new ErrorCustomException(ErrorCode.UNNECESSARY_VERSION_UPDATE);
        }
    }

    private static int convertVersion(String version) {
        String[] parts = version.split("\\.");
        StringBuilder versionNumber = new StringBuilder();
        for (String part : parts) {
            // Assuming that each part of the version number is less than 100.
            if (part.length() == 1) {
                versionNumber.append("0"); // Add leading zero if part has only one digit.
            }
            versionNumber.append(part);
        }
        return Integer.parseInt(versionNumber.toString());
    }

    private void checkForNecessaryUpdate(Optional<Version> requiredVersionOpt, int userVersionNum) {
        requiredVersionOpt.ifPresent(version -> {
            int requiredVersionNum = convertVersion(version.getVersionName());
            if (userVersionNum < requiredVersionNum) {
                throw new ErrorCustomException(ErrorCode.NECESSARY_VERSION_UPDATE);
            }
        });
    }

    private void checkForOptionalUpdate(Optional<Version> notifyVersionOpt, int userVersionNum) {
        notifyVersionOpt.ifPresent(version -> {
            int notifyVersionNum = convertVersion(version.getVersionName());
            if (userVersionNum < notifyVersionNum) {
                throw new ErrorCustomException(ErrorCode.UNNECESSARY_VERSION_UPDATE);
            }
        });
    }
}
