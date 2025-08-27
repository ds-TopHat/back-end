package com.mathfusion.domain.auth.controller;

import com.mathfusion.domain.auth.dto.KakaoRequestDTO;
import com.mathfusion.domain.auth.dto.KakaoResponseDTO;
import com.mathfusion.domain.auth.service.KakaoAuthService;
import com.mathfusion.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "카카오 소셜 로그인/회원가입 API",
        description = "카카오 소셜 로그인 회원 인가처리 API")
@RestController
@RequestMapping("/api/auth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;
    /**
     * 카카오 인가 코드를 받는 api
     */
    @Operation(
            summary = "카카오 인가 코드를 받는 API",
            description = "카카오 로그인을 위해 액세스 토큰을 프론트에서 받습니다. 액세스 토큰을 이용해서 사용자 정보를 받아서 로그인완료/회원가입 요청으로 분기됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/login")
    public ApiResponse<KakaoResponseDTO.KakaoLoginResponseDTO> kakaoCallback(@RequestBody KakaoRequestDTO.KakaoLoginRequestDTO request) {
        return ApiResponse.onSuccess(kakaoAuthService.processKakaoLogin(request));
    }

    /**
     * 카카오 회원가입 api
     */
    @Operation(
            summary = "카카오 회원가입 요청 API",
            description = "처음 로그인 시도한 카카오 회원의 회원 정보를 등록하기 위한 API," +
                    " 카카오 로그인의 응답 isNew 값이 True 이면 해당 API 요청을 보내야합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/signup")
    public ApiResponse<KakaoResponseDTO.KakaoLoginResponseDTO> kakaoSignup(@RequestBody KakaoRequestDTO.KakaoSignupRequestDTO request) {
        return ApiResponse.onSuccess(kakaoAuthService.signupKakaoMember(request));
    }


}
