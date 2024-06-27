package com.example.sampleroad.repository.product;

import com.example.sampleroad.domain.product.MdPick;
import com.example.sampleroad.dto.response.product.MdPickQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MdPickRepositoryCustom{
    Page<MdPickQueryDto.MdPickItemInfo> findMdKit(Pageable pageable);
}
