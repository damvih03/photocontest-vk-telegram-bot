package com.damvih.service;

import com.damvih.dto.ParticipantDto;
import com.damvih.dto.api.VkApiPhotoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalculationResultService {

    private final VkApiService vkApiService;

    public List<ParticipantDto> calculate(Long groupId, Long albumId) {

        List<ParticipantDto> participants = new ArrayList<>();

        List<Long> memberIds = vkApiService.getGroupMemberIds(groupId);
        List<VkApiPhotoResponse> photos = vkApiService.getPhotoIds(groupId, albumId);

        for (VkApiPhotoResponse photo: photos) {
            List<Long> likeIds =  vkApiService.getLikeIds(groupId, photo.getId());
            int count = getCount(memberIds, likeIds);
            int total = likeIds.size();
            participants.add(
                    new ParticipantDto(
                            photo.getId(),
                            photo.getText(),
                            count,
                            total)
            );
        }

        return participants;
    }

    public List<ParticipantDto> sort(List<ParticipantDto> participants) {
        return participants.stream()
                .sorted((p1, p2) -> p2.getCounted().compareTo(p1.getCounted()))
                .collect(Collectors.toList());
    }

    public List<ParticipantDto> getWinners(List<ParticipantDto> participants) {
        // We trust that the list is sorted in down and not empty
        Integer max = participants.getFirst().getCounted();
        return participants.stream()
                .takeWhile(p -> p.getCounted().equals(max))
                .collect(Collectors.toList());
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
