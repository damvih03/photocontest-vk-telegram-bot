package com.damvih.service;

import com.damvih.dto.ParticipantDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculationResultService {

    private final VkApiService vkApiService;

    public List<ParticipantDto> calculate(Long groupId, Long albumId) {

        List<ParticipantDto> participants = new ArrayList<>();

        List<Long> memberIds = vkApiService.getGroupMemberIds(groupId);
        List<Long> photoIds = vkApiService.getPhotoIds(groupId, albumId);

        for (Long photoId : photoIds) {
            List<Long> likeIds =  vkApiService.getLikeIds(groupId, photoId);
            int count = getCount(memberIds, likeIds);
            int total = likeIds.size();
            participants.add(
                    new ParticipantDto(photoId, count, total)
            );
        }

        return participants;
    }

    private int getCount(List<Long> memberIds, List<Long> likeIds) {
        int count = 0;
        for (Long likeId : likeIds) {
            if (memberIds.contains(likeId)) {
                count += 1;
            }
        }
        return count;
    }

}
