package com.example.reward_monitoring.mission.missionCS.controller;


import com.example.reward_monitoring.mission.missionCS.entity.MissionCS;
import com.example.reward_monitoring.mission.missionCS.repository.MissionCSRepository;
import com.example.reward_monitoring.mission.missionCS.service.MissionCSService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@Tag(name = "MissionCS", description = "미션CS관리 API")
public class MissionCSController {
    @Autowired
    private MissionCSRepository missionCSRepository;

    @Autowired
    private MissionCSService missionCSService;

    @GetMapping("MissionCS/{idx}")  //미션 검색 (idx)
    public ResponseEntity<MissionCS> getMissionCS(@PathVariable int idx){
        MissionCS target = missionCSService.getMissionCS(idx);
        return (target != null) ?
                ResponseEntity.status(HttpStatus.OK).body(target):
                ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/missionCS/missionCSs")  //전체 광고주 리스트 반환
    public ResponseEntity<List<MissionCS>> getAns(){
        return ResponseEntity.status(HttpStatus.OK).body(missionCSService.getMissionCSs());
    }

    @DeleteMapping("missionCS/delete/{idx}")  // DELETE
    public ResponseEntity<String> delete(@PathVariable int idx)throws IOException {

        MissionCS deleted  = missionCSService.delete(idx);
        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build():
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }
}
