package com.dku.mentoring.register.service;

import com.dku.mentoring.mission.model.entity.Mission;
import com.dku.mentoring.mission.repository.MissionRepository;
import com.dku.mentoring.register.model.dto.list.SummarizedRegisterDto;
import com.dku.mentoring.register.model.dto.request.RegisterRequestDto;
import com.dku.mentoring.register.model.dto.response.ResponseSingleRegisterDto;
import com.dku.mentoring.register.model.entity.Register;
import com.dku.mentoring.register.repository.RegisterRepository;
import com.dku.mentoring.user.entity.User;
import com.dku.mentoring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegisterService {
    private final RegisterRepository registerRepository;
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;

    /**
     * 미션 인증 글 등록
     *
     * @param userId 등록한 사용자 id
     * @param dto    등록할 글 dto
     */
    @Transactional
    public Long createRegister(Long userId, RegisterRequestDto dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
        Mission mission = missionRepository.findById(dto.getMissionId()).orElseThrow(() -> new IllegalArgumentException("해당 미션이 없습니다."));

        Register register = dto.toEntity(user, mission);

        Register savedPost = registerRepository.save(register);
        return savedPost.getId();
    }

    /**
     * 미션 인증 글 상세 조회
     *
     * @param registerId 조회할 글 id
     */
    public ResponseSingleRegisterDto findOne(Long registerId) {
        Register register = registerRepository.findById(registerId).orElseThrow(() -> new IllegalArgumentException("해당 글이 없습니다."));
        return new ResponseSingleRegisterDto(register);
    }

    /**
     * 전체 등록 글 조회
     *
     * @param pageable 페이징 방법
     *
     * @return 페이징된 목록
     */
    public Page<SummarizedRegisterDto> getRegisters(Pageable pageable) {
        Page<Register> registers = registerRepository.findAll(pageable);
        return registers.map(SummarizedRegisterDto::new);
    }

    /**
     * 사용자가 등록 글 전체 조회
     *
     *  @param userId 사용자 id
     */
    public Page<SummarizedRegisterDto> getRegistersByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
        if(!user.getId().equals(userId))
            throw new IllegalArgumentException("해당 권한이 없습니다.");
        Page<Register> registers = registerRepository.findByUserId(userId, pageable);
        return registers.map(SummarizedRegisterDto::new);
    }

    /**
     * 등록 글 수정
     *
     * @param registerId 수정할 글 id
     * @param userId     수정한 사용자 id
     */
    @Transactional
    public Long updateRegister(Long registerId, Long userId, RegisterRequestDto dto) {
        Register register = registerRepository.findById(registerId).orElseThrow(() -> new IllegalArgumentException("해당 글이 없습니다."));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
        if(!register.getUser().getId().equals(userId))
            throw new IllegalArgumentException("해당 권한이 없습니다.");
        register.update(dto);
        return register.getId();
    }

    /**
     * 등록 글 삭제
     *
     * @param registerId 삭제할 글 id
     * @param userId     삭제한 사용자 id
     */
    @Transactional
    public void deleteRegister(Long registerId, Long userId) {
        Register register = registerRepository.findById(registerId).orElseThrow(() -> new IllegalArgumentException("해당 글이 없습니다."));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
        if(!register.getUser().getId().equals(userId))
            throw new IllegalArgumentException("해당 권한이 없습니다.");
        registerRepository.delete(register);
    }
}
