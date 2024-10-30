package com.example.reward_monitoring.mission.answerMsn.service;

import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.repository.AdvertiserRepository;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.repository.ServerRepository;
import com.example.reward_monitoring.mission.answerMsn.dto.*;
import com.example.reward_monitoring.mission.answerMsn.entity.AnswerMsn;
import com.example.reward_monitoring.mission.answerMsn.repository.AnswerMsnRepository;
import com.example.reward_monitoring.statistics.answerMsnStat.daily.entity.AnswerMsnDailyStat;
import com.example.reward_monitoring.statistics.answerMsnStat.daily.repository.AnswerMsnDailyStatRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AnswerMsnService {

    @Autowired
    private AnswerMsnRepository answerMsnRepository;
    @Autowired
    private AnswerMsnDailyStatRepository answerMsnDailyStatRepository;
    @Autowired
    private AdvertiserRepository advertiserRepository;
    @Autowired
    private ServerRepository serverRepository;

    public AnswerMsn edit(int idx, AnswerMsnEditDto dto) {
        AnswerMsn answerMsn = answerMsnRepository.findByIdx(idx);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

        if(answerMsn==null)
            return null;
        if(dto.getMissionDefaultQty() != null)
            answerMsn.setMissionDefaultQty(dto.getMissionDefaultQty());
        if(dto.getMissionDailyCap() !=null)
            answerMsn.setMissionDailyCap(dto.getMissionDailyCap());
        if(dto.getMissionExpOrder()!=null)
            answerMsn.setMissionExpOrder(dto.getMissionExpOrder());
        if(dto.getAdvertiser()!=null)
            answerMsn.setAdvertiser(advertiserRepository.findByAdvertiser_(dto.getAdvertiser()));
        if(dto.getAdvertiserDetails()!=null)
            answerMsn.setAdvertiserDetails(dto.getAdvertiserDetails());
        if(dto.getMissionTitle()!=null)
            answerMsn.setMissionTitle(dto.getMissionTitle());
        if(dto.getMissionContent()!=null && !(dto.getMissionContent().isEmpty()))
            answerMsn.setMissionContent(dto.getMissionContent());
        if(dto.getMissionDetailTitle()!=null)
            answerMsn.setMissionDetailTitle(dto.getMissionDetailTitle());
        if(dto.getMissionAnswer()!=null)
            answerMsn.setMissionAnswer(dto.getMissionAnswer());
        if (dto.getStartAtMsnDate() != null && dto.getStartTime() != null) {
            LocalDate date = LocalDate.parse(dto.getStartAtMsnDate(), dateFormatter);
            LocalTime time = LocalTime.parse(dto.getStartTime(), timeFormatter);
            dto.setStartAtMsn(LocalDateTime.of(date,time));
            answerMsn.setStartAtMsn(dto.getStartAtMsn());
        }
        if (dto.getEndAtMsnDate() != null && dto.getEndTime() != null) {
            LocalDate date = LocalDate.parse(dto.getEndAtMsnDate(), dateFormatter);
            LocalTime time = LocalTime.parse(dto.getEndTime(), timeFormatter);
            dto.setEndAtMsn(LocalDateTime.of(date,time));
            answerMsn.setEndAtMsn(dto.getEndAtMsn());
        }


        if (dto.getStartAtCap() != null)
            answerMsn.setStartAtCap(dto.getStartAtCap());
        if (dto.getEndAtCap() != null)
            answerMsn.setEndAtCap(dto.getEndAtCap());
        if (dto.getMissionActive() != null) {
            boolean bool = dto.getMissionActive();
            answerMsn.setMissionActive(bool);
        }
        if (dto.getMissionExposure() != null) {
            boolean bool = dto.getMissionExposure();
            answerMsn.setMissionExposure(bool);
        }
        if (dto.getDupParticipation() != null) {
            boolean bool = dto.getDupParticipation();
            answerMsn.setDupParticipation(bool);
            if(!answerMsn.isDupParticipation())
                answerMsn.setReEngagementDay(null);
        }
        if (dto.getReEngagementDay() != null)
            answerMsn.setReEngagementDay(dto.getReEngagementDay());
        if(dto.getExceptMedia() !=null && !(dto.getExceptMedia().isEmpty())) {
            answerMsn.setExceptMedia(answerMsn.convertJsonToString(answerMsn.convertDataToJson(dto.getExceptMedia())));
        }
        if(dto.getMsnUrl1()!=null && !(dto.getMsnUrl1().isEmpty())) {
            answerMsn.setMsnUrl1(dto.getMsnUrl1());
        }
        if(dto.getMsnUrl2()!=null && !(dto.getMsnUrl2().isEmpty()))
            answerMsn.setMsnUrl2(dto.getMsnUrl2());
        if(dto.getMsnUrl3()!=null && !(dto.getMsnUrl3().isEmpty()))
            answerMsn.setMsnUrl3(dto.getMsnUrl3());
        if(dto.getMsnUrl4()!=null && !(dto.getMsnUrl4().isEmpty()))
            answerMsn.setMsnUrl4(dto.getMsnUrl4());
        if(dto.getMsnUrl5()!=null && !(dto.getMsnUrl5().isEmpty()))
            answerMsn.setMsnUrl5(dto.getMsnUrl5());
        if(dto.getMsnUrl6()!=null && !(dto.getMsnUrl6().isEmpty()))
            answerMsn.setMsnUrl6(dto.getMsnUrl6());
        if(dto.getMsnUrl7()!=null && !(dto.getMsnUrl7().isEmpty()))
            answerMsn.setMsnUrl7(dto.getMsnUrl7());
        if(dto.getMsnUrl8()!=null && !(dto.getMsnUrl8().isEmpty()))
            answerMsn.setMsnUrl8(dto.getMsnUrl8());
        if(dto.getMsnUrl9()!=null && !(dto.getMsnUrl9().isEmpty()))
            answerMsn.setMsnUrl9(dto.getMsnUrl9());
        if(dto.getMsnUrl10()!=null && !(dto.getMsnUrl10().isEmpty()))
            answerMsn.setMsnUrl10(dto.getMsnUrl10());
        if(dto.getDataType()!=null) {
            boolean bool = dto.getDataType();
            answerMsn.setDataType(bool);
        }
        if(dto.getImageName()!=null && !(dto.getImageName().isEmpty())){
            answerMsn.setImageName(dto.getImageName());
        }

        return answerMsn;
    }

    public AnswerMsn add(AnswerMsnReadDto dto) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
        Server serverEntity = null;
        if (dto.getStartAtMsnDate() != null && dto.getStartTime() != null) {

            LocalDate date = LocalDate.parse(dto.getStartAtMsnDate(), dateFormatter);
            LocalTime time = LocalTime.parse(dto.getStartTime(), timeFormatter);
            dto.setStartAtMsn(LocalDateTime.of(date,time));
        }
        if (dto.getEndAtMsnDate() != null && dto.getEndTime() != null) {

            LocalDate date = LocalDate.parse(dto.getEndAtMsnDate(), dateFormatter);
            LocalTime time = LocalTime.parse(dto.getEndTime(), timeFormatter);
            dto.setEndAtMsn(LocalDateTime.of(date,time));
        }
        if(dto.getUrl()!= null)
            serverEntity = serverRepository.findByServerUrl_(dto.getUrl());
        else
            serverEntity = serverRepository.findByServerUrl_("https://ocb.srk.co.kr");  //default 서버 주소

        Advertiser advertiserEntity = advertiserRepository.findByAdvertiser_(dto.getAdvertiser());
        if(!dto.getDupParticipation())
            dto.setReEngagementDay(null);

        dto.setDataType(true);
        return dto.toEntity(advertiserEntity,serverEntity);
    }

    public AnswerMsn getAnswerMsn(int idx) {
        return answerMsnRepository.findByIdx(idx);
    }

    public List<AnswerMsn> getAnswerMsns() {
        return answerMsnRepository.findAllMission();

    }

    public AnswerMsn delete(int idx) {
        AnswerMsn target = answerMsnRepository.findByIdx(idx);
        if(target==null)
            return null;
        answerMsnRepository.delete(target);
        return target;
    }

    //검색 조건에 맞는 정답 미션을 검색
    public List<AnswerMsn> searchAnswerMsn(AnswerMsnSearchDto dto) {

        List<AnswerMsn> target_date = null;
        List<AnswerMsn> target_dailyCap = null;

        List<AnswerMsn> target_dup_Participation = null;
        List<AnswerMsn> target_mission_active = null;
        List<AnswerMsn> target_mission_exposure = null;
        List<AnswerMsn> target_data_Type = null;

        List<AnswerMsn> target_advertiser = null;
        List<AnswerMsn> target_advertiser_details = null; // 선택 1
        List<AnswerMsn> target_mission_title = null; // 선택 2


        List<AnswerMsn> result;
        boolean changed = false;

        if(dto.getStartAtMsn() != null || dto.getEndAtMsn() != null){
            if(dto.getStartAtMsn() != null){

                LocalDateTime start_time = dto.getStartAtMsn().atStartOfDay();
                if(dto.getEndAtMsn() == null){
                    target_date = answerMsnRepository.findByStartDate(start_time);
                }else{
                    LocalDateTime end_time = dto.getEndAtMsn().atTime(23, 59);
                    target_date = answerMsnRepository.findByBothDate(start_time,end_time);
                }
            }
            else {
                LocalDateTime end_time = dto.getEndAtMsn().atTime(23, 59);
                target_date = answerMsnRepository.findByEndDate(end_time);
            }

        }

        if(dto.getStartAtCap() != null || dto.getEndAtCap() != null) {
            if (dto.getStartAtCap() != null) {
                if (dto.getEndAtCap() == null) {
                    target_dailyCap = answerMsnRepository.findByStartAtCap(dto.getStartAtCap());
                } else {
                    target_dailyCap = answerMsnRepository.findByBothCap(dto.getStartAtCap(), dto.getEndAtCap());
                }

            } else {
                target_dailyCap = answerMsnRepository.findByEndAtCap(dto.getEndAtCap());
            }
        }

        if(dto.getDupParticipation()!=null){
            target_dup_Participation = answerMsnRepository.findByDupParticipation(dto.getDupParticipation());
        }

        if(dto.getMissionActive()!=null){
            target_mission_active = answerMsnRepository.findByMissionActive(dto.getMissionActive());
        }

        if(dto.getMissionExposure() !=null ){
            target_mission_exposure = answerMsnRepository.findByMissionExposure(dto.getMissionExposure());
        }

        if(dto.getDataType() != null)
            target_data_Type = answerMsnRepository.findByDataType(dto.getDataType());

        if(dto.getAdvertiser() != null)
            target_advertiser = answerMsnRepository.findByAdvertiser(dto.getAdvertiser());

        if(dto.getAdvertiserDetails() != null  && !dto.getAdvertiserDetails().isEmpty())
            target_advertiser_details = answerMsnRepository.findByAdvertiserDetails(dto.getAdvertiserDetails());

        if(dto.getMissionTitle() != null && !dto.getMissionTitle().isEmpty())
            target_mission_title = answerMsnRepository.findByMissionTitle(dto.getMissionTitle());

        result = new ArrayList<>(answerMsnRepository.findAll());

        if(target_date != null){
            Set<Integer> idxSet = target_date.stream().map(AnswerMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_dailyCap !=null) {
            Set<Integer> idxSet = target_dailyCap.stream().map(AnswerMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }


        if(target_dup_Participation!=null){
            Set<Integer> idxSet = target_dup_Participation.stream().map(AnswerMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }else{
            target_dup_Participation = answerMsnRepository.findAll();
            Set<Integer> idxSet = target_dup_Participation.stream().map(AnswerMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_mission_active != null) {
            Set<Integer> idxSet = target_mission_active.stream().map(AnswerMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }else{
            target_mission_active = answerMsnRepository.findAll();
            Set<Integer> idxSet = target_mission_active.stream().map(AnswerMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_mission_exposure != null) {
            Set<Integer> idxSet = target_mission_exposure.stream().map(AnswerMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }else{
            target_mission_active = answerMsnRepository.findAll();
            Set<Integer> idxSet = target_mission_active.stream().map(AnswerMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }


        if(target_data_Type != null) {
            Set<Integer> idxSet = target_data_Type.stream().map(AnswerMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_advertiser != null) {
            Set<Integer> idxSet = target_advertiser.stream().map(AnswerMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }else{
            target_advertiser = answerMsnRepository.findAll();
            Set<Integer> idxSet = target_advertiser.stream().map(AnswerMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_advertiser_details != null) {
            Set<Integer> idxSet = target_advertiser_details.stream().map(AnswerMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        else if(target_mission_title !=null) {
            Set<Integer> idxSet = target_mission_title.stream().map(AnswerMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(!changed)
            result = new ArrayList<>();
        return result;

    }

    public Sheet excelDownload( List<AnswerMsn> list,Workbook wb){

        int size = list.size();
        Sheet sheet = wb.createSheet("정답 미션 목록");
        Row row = null;
        Cell cell = null;
        CellStyle cellStyle = wb.createCellStyle();
        applyCellStyle(cellStyle);
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("quizIdx");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("기본 수량");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(2);
        cell.setCellValue("데일리캡");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(3);
        cell.setCellValue("광고주");
        sheet.setColumnWidth(3, 16 * 256); //8자
        cell.setCellStyle(cellStyle);
        cell = row.createCell(4);
        cell.setCellValue("광고주 상세");
        sheet.setColumnWidth(4, 16 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(5);
        cell.setCellValue("미션 제목");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(5, 16 * 256);
        cell = row.createCell(6);
        cell.setCellValue("미션 정답");
        sheet.setColumnWidth(6, 20 * 256); //8자
        cell.setCellStyle(cellStyle);
        cell = row.createCell(7);
        cell.setCellValue("미션 시작일시");
        sheet.setColumnWidth(7, 20 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(8);
        cell.setCellValue("미션 종료일시");
        sheet.setColumnWidth(8, 20 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(9);
        cell.setCellValue("데일리캡 시작일");
        sheet.setColumnWidth(9, 20 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(10);
        cell.setCellValue("데일리캡 종료일");
        sheet.setColumnWidth(10, 20 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(11);
        cell.setCellValue("미션 사용여부");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(12);
        cell.setCellValue("미션 노출여부");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(13);
        cell.setCellValue("중복참여");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(14);
        cell.setCellValue("재참여 가능일");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(14, 20 * 256);

        for (AnswerMsn answerMsn : list) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(answerMsn.getIdx());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(1);
            cell.setCellValue(answerMsn.getMissionDefaultQty());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellValue(answerMsn.getMissionDailyCap());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(3);
            cell.setCellValue(answerMsn.getAdvertiser().getAdvertiser());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(4);
            cell.setCellValue(answerMsn.getAdvertiserDetails());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(answerMsn.getMissionTitle());
            cell.setCellStyle(cellStyle);
            cell = row.createCell(6);
            cell.setCellValue(answerMsn.getMissionAnswer());
            cell.setCellStyle(cellStyle);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            cell = row.createCell(7);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(answerMsn.getStartAtMsn().format(formatter));
            cell = row.createCell(8);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(answerMsn.getEndAtMsn().format(formatter));
            cell = row.createCell(9);
            cell.setCellStyle(cellStyle);
            DateTimeFormatter formatter_ = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            cell.setCellValue(answerMsn.getStartAtCap().format(formatter_));
            cell.setCellStyle(cellStyle);
            cell = row.createCell(10);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(answerMsn.getEndAtCap().format(formatter_));
            cell = row.createCell(11);
            cell.setCellStyle(cellStyle);
            if(answerMsn.isMissionActive())
                cell.setCellValue("활성");
            else
                cell.setCellValue("비활성");
            cell = row.createCell(12);
            cell.setCellStyle(cellStyle);
            if(answerMsn.isMissionExposure())
                cell.setCellValue("노출");
            else
                cell.setCellValue("비노출");
                    
            cell = row.createCell(13);
            cell.setCellStyle(cellStyle);
            if(answerMsn.isDupParticipation())
                cell.setCellValue("중복 허용");
            else
                cell.setCellValue("중복 불가");
            cell = row.createCell(14);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(answerMsn.getReEngagementDay());
        }
        return sheet;
    }

    private void applyCellStyle(CellStyle cellStyle) {
//        XSSFCellStyle xssfCellStyle = (XSSFCellStyle) cellStyle;
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
    }

    @Transactional
    public boolean readExcel(MultipartFile file)throws IOException{

        Workbook workbook;
        DecimalFormat df = new DecimalFormat("0");
        String fileName = file.getOriginalFilename();

        // 확장자에 따라 Workbook 결정
        try (InputStream is = file.getInputStream()) {
            if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(is); // For OOXML (.xlsx)
            } else if (fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(is); // For OLE2 (.xls)
            } else {
                throw new IllegalArgumentException("지원되지 않는 파일 형식입니다: " + fileName);
            }
        }

        Sheet worksheet = workbook.getSheetAt(0);
        AnswerMsnReadDto dto = new AnswerMsnReadDto();
        Advertiser advertiserEntity = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter_date = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for(int i = worksheet.getPhysicalNumberOfRows() - 1; i >= 1; i--) {

            Row row = worksheet.getRow(i);

            if (row.getCell(1) != null) {
                dto.setMissionDefaultQty((int) row.getCell(1).getNumericCellValue());
            }
            if(row.getCell(2)!=null)
                dto.setMissionDailyCap((int)row.getCell(2).getNumericCellValue());

            advertiserEntity = advertiserRepository.findByAdvertiser_(row.getCell(3).getStringCellValue());
                //셀에있는 데이터를 읽어와 그걸로 repository 에서 일치하는 advertiser 를 가져온다.
            if(row.getCell(4)!=null) {
                if (row.getCell(4).getCellType() == CellType.STRING)
                    dto.setAdvertiserDetails(row.getCell(4).getStringCellValue());
                else
                    dto.setAdvertiserDetails(df.format(row.getCell(4).getNumericCellValue()));
            }
            if(row.getCell(5)!=null)
                dto.setMissionTitle(row.getCell(5).getStringCellValue());

            if(row.getCell(6)!=null) {
                if (row.getCell(6).getCellType() == CellType.STRING)
                    dto.setMissionAnswer(row.getCell(6).getStringCellValue());
                else
                    dto.setMissionAnswer(df.format(row.getCell(6).getNumericCellValue()));
            }
            if(row.getCell(7)!=null)
                dto.setEndAtMsn(LocalDateTime.parse(row.getCell(7).getStringCellValue(), formatter));
            if(row.getCell(8)!=null)
                dto.setEndAtMsn(LocalDateTime.parse(row.getCell(8).getStringCellValue(), formatter));
            if(row.getCell(9)!=null)
                dto.setStartAtCap(LocalDate.parse(row.getCell(9).getStringCellValue(),formatter_date));
            if(row.getCell(10)!=null)
                dto.setEndAtCap(LocalDate.parse(row.getCell(10).getStringCellValue(),formatter_date));

            if(Objects.equals(row.getCell(11).getStringCellValue(), "활성"))
                dto.setMissionActive(true);
            else
                dto.setMissionActive(false);
            if(Objects.equals(row.getCell(12).getStringCellValue(), "노출"))
                dto.setMissionExposure(true);
            else
                dto.setMissionExposure(false);
            if(Objects.equals(row.getCell(13).getStringCellValue(),"중복 허용"))
                dto.setDupParticipation(true);
            else
                dto.setDupParticipation(false);
            dto.setReEngagementDay((int)row.getCell(14).getNumericCellValue());

            answerMsnRepository.save(dto.toEntity(advertiserEntity,null));

        }
        return true;
    }

    public boolean allMissionEnd() {

        List<AnswerMsn> target = getAnswerMsns();
        if(target == null)
            return false;

        for(AnswerMsn answerMsn : target){
            answerMsn.setMissionExposure(false);
            answerMsn.setMissionActive(false);
            answerMsnRepository.save(answerMsn);
        }
        return true;
    }



    public boolean changeAbleDay(AnswerMsnAbleDayDto dto, int idx) {
        AnswerMsn answerMsn = answerMsnRepository.findByIdx(idx);
        if(answerMsn == null)
            return false;

        answerMsn.setDupParticipation(dto.isDupParticipation());
        if(dto.getReEngagementDay()!=null)
            answerMsn.setReEngagementDay(dto.getReEngagementDay());
        answerMsnRepository.save(answerMsn);
        return true;

    }

    public boolean changeMissionReEngagementDay(int idx, AnswerMsnAbleDayDto dto) {
        AnswerMsn target = answerMsnRepository.findByIdx(idx);
        if(target ==null)
            return false;
       target.setDupParticipation(dto.isDupParticipation());
       if(!dto.isDupParticipation())
           target.setReEngagementDay(null);
       else
        target.setReEngagementDay(dto.getReEngagementDay());

       answerMsnRepository.save(target);
        return true;
    }

    public boolean changeMissionExpose(int idx, AnswerMsnExposeDto dto) {

        AnswerMsn target = answerMsnRepository.findByIdx(idx);
        if(target ==null)
            return false;
        target.setMissionExposure(dto.isExpose());
        answerMsnRepository.save(target);
        return true;
    }

    public boolean setOffMissionIsUsed(int idx) {

        List<AnswerMsn> answerMsns = getAnswerMsns();
        Collections.reverse(answerMsns);

        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (idx - 1) * limit;


        // 전체 리스트의 크기 체크
        List<AnswerMsn> limitedAnswerMsns;
        if (startIndex < answerMsns.size()) {
            int endIndex = Math.min(startIndex + limit, answerMsns.size());
            limitedAnswerMsns = answerMsns.subList(startIndex, endIndex);
        } else {
            return false;
        }
        for (AnswerMsn answerMsn : limitedAnswerMsns) {
            answerMsn.setMissionActive(false); // isUsed 필드를 false로 설정
            answerMsnRepository.save(answerMsn);
        }
        return true;
    }

    public boolean setOffMissionIsUsed(int idx,List<AnswerMsn> target) {
        for (AnswerMsn answerMsn : target) {
            answerMsn.setMissionActive(false); // isUsed 필드를 false로 설정
            answerMsnRepository.save(answerMsn);
        }
        return true;
    }


    public boolean setOffMissionIsView(int idx) {
        List<AnswerMsn> answerMsns = getAnswerMsns();
        Collections.reverse(answerMsns);

        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (idx - 1) * limit;

        // 전체 리스트의 크기 체크
        List<AnswerMsn> limitedAnswerMsns;
        if (startIndex < answerMsns.size()) {
            int endIndex = Math.min(startIndex + limit, answerMsns.size());
            limitedAnswerMsns = answerMsns.subList(startIndex, endIndex);
        } else {
            return false;
        }
        for (AnswerMsn answerMsn : limitedAnswerMsns) {
            answerMsn.setMissionExposure(false); // missionExpose 필드를 false로 설정
            answerMsnRepository.save(answerMsn);
        }
        return true;
    }

    public boolean setOffMissionIsView(int idx,List<AnswerMsn> target) {
        for (AnswerMsn answerMsn : target) {
            answerMsn.setMissionExposure(false); // missionExpose 필드를 false로 설정
            answerMsnRepository.save(answerMsn);
        }
        return true;
    }



    public boolean AllOffMission() {

        List<AnswerMsn> answerMsns = getAnswerMsns();
        for (AnswerMsn answerMsn : answerMsns) {
            answerMsn.setMissionActive(false); // isUsed 필드를 false로 설정
            answerMsn.setMissionExposure(false);
            answerMsnRepository.save(answerMsn);
        }
        return true;
    }


    public boolean setMissionIsUsed(int idx) {
        AnswerMsn target= answerMsnRepository.findByIdx(idx);
        if(target == null)
            return false;
        target.setMissionActive(true);
        answerMsnRepository.save(target);
        return true;
    }

    public boolean setMissionIsUsedFalse(int idx) {
        AnswerMsn target= answerMsnRepository.findByIdx(idx);
        if(target == null)
            return false;
        target.setMissionActive(false);
        answerMsnRepository.save(target);
        return true;
    }


    public boolean setMissionIsView(int idx) {
        AnswerMsn target= answerMsnRepository.findByIdx(idx);
        if(target == null)
            return false;
        target.setMissionExposure(true);
        answerMsnRepository.save(target);
        return true;
    }

    public boolean setMissionIsViewFalse(int idx) {
        AnswerMsn target= answerMsnRepository.findByIdx(idx);
        if(target == null)
            return false;
        target.setMissionExposure(false);
        answerMsnRepository.save(target);
        return true;
    }



    public boolean hidden(int idx) {

        AnswerMsn target= answerMsnRepository.findByIdx(idx);
        if(target == null)
            return false;
        target.setDataType(false);
        answerMsnRepository.save(target);
        return true;
    }

    public List<AnswerMsn> searchAnswerMsnCurrent(AnswerMsnSearchByConsumedDto dto) {
        List<AnswerMsn> target_advertiser = null;
        List<AnswerMsn> target_serverUrl = null;
        List<AnswerMsn> target_advertiser_details = null; // 선택 1
        List<AnswerMsn> target_mission_title = null; // 선택 2



        List<AnswerMsn> result;
        boolean changed = false;

        if(dto.getAdvertiser()!=null){
            target_advertiser = answerMsnRepository.findByAdvertiser(dto.getAdvertiser());
        }

        if(dto.getServerUrl()!=null){
            target_serverUrl = answerMsnRepository.findByServer(dto.getServerUrl());
        }

        if(dto.getAdvertiserDetails() != null  && !dto.getAdvertiserDetails().isEmpty())
            target_advertiser_details = answerMsnRepository.findByAdvertiserDetails(dto.getAdvertiserDetails());

        if(dto.getMissionTitle() != null && !dto.getMissionTitle().isEmpty())
            target_mission_title = answerMsnRepository.findByMissionTitle(dto.getMissionTitle());

        LocalDateTime now = LocalDateTime.now();
        result = new ArrayList<>(answerMsnRepository.findByCurrentList(now));


        if(target_serverUrl !=null) {
            Set<Integer> idxSet = target_serverUrl.stream().map(AnswerMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_advertiser != null) {
            Set<Integer> idxSet = target_advertiser.stream().map(AnswerMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_advertiser_details != null) {
            Set<Integer> idxSet = target_advertiser_details.stream().map(AnswerMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        else if(target_mission_title !=null) {
            Set<Integer> idxSet = target_mission_title.stream().map(AnswerMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsn -> idxSet.contains(answerMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(!changed)
            result = new ArrayList<>();
        return result;
    }

    public boolean AllOffMissionCurrent() {
        LocalDateTime now = LocalDateTime.now();
        List<AnswerMsn> answerMsns = answerMsnRepository.findByCurrentList(now);
        for (AnswerMsn answerMsn : answerMsns) {
            answerMsn.setMissionActive(false); // isUsed 필드를 false로 설정
            answerMsn.setMissionExposure(false);
            answerMsnRepository.save(answerMsn);
        }
        return true;
    }

    

    public Sheet excelDownloadCurrent( List<AnswerMsn> list,Workbook wb){

        int size = list.size();
        Sheet sheet = wb.createSheet("정답 미션 목록");
        Row row = null;
        Cell cell = null;
        CellStyle cellStyle = wb.createCellStyle();
        applyCellStyle(cellStyle);
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("quizIdx");
        cell.setCellStyle(cellStyle);
        
        cell = row.createCell(1);
        cell.setCellValue("광고주");
        sheet.setColumnWidth(1, 16 * 256); //8자
        cell.setCellStyle(cellStyle);
        

        cell = row.createCell(2);
        cell.setCellValue("미션 제목");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(2, 16 * 256);

        cell = row.createCell(3);
        cell.setCellValue("기본 수량");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("데일리캡");
        cell.setCellStyle(cellStyle);
        
        cell = row.createCell(5);
        cell.setCellValue("전체 랜딩수");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(6);
        cell.setCellValue("전체 참여수");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(7);
        cell.setCellValue("미션 시작일시");
        sheet.setColumnWidth(6, 20 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(8);
        cell.setCellValue("미션 종료일시");
        sheet.setColumnWidth(7, 20 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(9);
        cell.setCellValue("데일리캡 시작일");
        sheet.setColumnWidth(8, 20 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(10);
        cell.setCellValue("데일리캡 종료일");
        sheet.setColumnWidth(9, 20 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(11);
        cell.setCellValue("미션 상태");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(12);
        cell.setCellValue("미션 노출여부");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(13);
        cell.setCellValue("중복참여");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(14);
        cell.setCellValue("재참여 가능일");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(14, 20 * 256);

        for (AnswerMsn answerMsn : list) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(answerMsn.getIdx());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(1);
            cell.setCellValue(answerMsn.getAdvertiserDetails());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(2);
            cell.setCellValue(answerMsn.getMissionTitle());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(3);
            cell.setCellValue(answerMsn.getMissionDefaultQty());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(4);
            cell.setCellValue(answerMsn.getMissionDailyCap());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(5);
            cell.setCellValue(answerMsn.getTotalLandingCnt());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(6);
            cell.setCellValue(answerMsn.getTotalPartCnt());
            cell.setCellStyle(cellStyle);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            cell = row.createCell(7);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(answerMsn.getStartAtMsn().format(formatter));

            cell = row.createCell(8);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(answerMsn.getEndAtMsn().format(formatter));

            cell = row.createCell(9);
            cell.setCellStyle(cellStyle);
            DateTimeFormatter formatter_ = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            cell.setCellValue(answerMsn.getStartAtCap().format(formatter_));
            cell.setCellStyle(cellStyle);

            cell = row.createCell(10);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(answerMsn.getEndAtCap().format(formatter_));
            cell.setCellStyle(cellStyle);

            cell = row.createCell(11);
            cell.setCellStyle(cellStyle);
            if(answerMsn.isMissionActive())
                cell.setCellValue("활성");
            else
                cell.setCellValue("비활성");

            cell = row.createCell(12);
            cell.setCellStyle(cellStyle);
            if(answerMsn.isMissionExposure())
                cell.setCellValue("노출");
            else
                cell.setCellValue("비노출");

            cell = row.createCell(13);
            cell.setCellStyle(cellStyle);
            if(answerMsn.isDupParticipation())
                cell.setCellValue("중복 허용");
            else
                cell.setCellValue("중복 불가");

            cell = row.createCell(14);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(answerMsn.getReEngagementDay());
        }
        return sheet;
    }



    //검색 조건에 맞는 리포트를 검색
    public List<AnswerMsnDailyStat> searchReport(quizStaticListSearchDto dto,int idx) {

        List<AnswerMsnDailyStat> target_url = null;
        List<AnswerMsnDailyStat> target_date = null;
        List<AnswerMsnDailyStat> target_idx = answerMsnDailyStatRepository.findByMsnIdx_(idx);


        List<AnswerMsnDailyStat> result;
        boolean changed = false;

        if(dto.getStartDate() != null || dto.getEndDate() != null) {
            if (dto.getStartDate() != null) {
                if (dto.getEndDate() == null) {
                    target_date  = answerMsnDailyStatRepository.findByStartAt(dto.getStartDate());
                } else {
                    target_date  = answerMsnDailyStatRepository.findByBothAt(dto.getStartDate(), dto.getEndDate());
                }

            } else {
                target_date  = answerMsnDailyStatRepository.findByEndAt(dto.getEndDate());
            }
        }

        if(dto.getUrl() != null)
            target_url = answerMsnDailyStatRepository.findByServer_ServerUrl(dto.getUrl());




        result = new ArrayList<>(answerMsnDailyStatRepository.findAll());

        if(target_idx  !=null) {
            Set<Integer> idxSet = target_idx.stream().map(AnswerMsnDailyStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnDailyStat -> idxSet.contains(answerMsnDailyStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }


        if(target_date  !=null) {
            Set<Integer> idxSet = target_date.stream().map(AnswerMsnDailyStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnDailyStat -> idxSet.contains(answerMsnDailyStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_url  !=null) {
            Set<Integer> idxSet = target_url.stream().map(AnswerMsnDailyStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(answerMsnDailyStat -> idxSet.contains(answerMsnDailyStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }


        if(!changed)
            result = new ArrayList<>();

        return result;
    }


    public Sheet reportExcelDownload(List<AnswerMsnDailyStat> list, Workbook wb){

        int size = list.size();
        Sheet sheet = wb.createSheet("정답 미션 목록");
        Row row = null;
        Cell cell = null;
        CellStyle cellStyle = wb.createCellStyle();
        applyCellStyle(cellStyle);
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("일시");
        sheet.setColumnWidth(1, 16 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("랜딩 카운트");
        sheet.setColumnWidth(1, 16 * 256); //8자
        cell.setCellStyle(cellStyle);


        cell = row.createCell(2);
        cell.setCellValue("참여 카운트");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(2, 16 * 256);

        for (AnswerMsnDailyStat answerMsnDailyStat : list) {

            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            DateTimeFormatter formatter_ = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            cell.setCellValue(answerMsnDailyStat.getPartDate().format(formatter_));
            cell.setCellStyle(cellStyle);

            cell = row.createCell(1);
            cell.setCellValue(answerMsnDailyStat.getLandingCnt());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(2);
            cell.setCellValue(answerMsnDailyStat.getPartCnt());
            cell.setCellStyle(cellStyle);

        }
        return sheet;
    }

    public Sheet downloadQuizForm(Workbook wb){


        Sheet sheet = wb.createSheet("정답 미션 목록");
        Row row = null;
        Cell cell = null;
        CellStyle cellStyle = wb.createCellStyle();
        applyCellStyle(cellStyle);
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("quizIdx(공란)");
        sheet.setColumnWidth(0, 16 * 256); //8자
        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("기본 수량");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(2);
        cell.setCellValue("데일리캡");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(3);
        cell.setCellValue("광고주");
        sheet.setColumnWidth(3, 16 * 256); //8자
        cell.setCellStyle(cellStyle);
        cell = row.createCell(4);
        cell.setCellValue("광고주 상세");
        sheet.setColumnWidth(4, 16 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(5);
        cell.setCellValue("미션 제목");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(5, 16 * 256);
        cell = row.createCell(6);
        cell.setCellValue("미션 정답");
        sheet.setColumnWidth(6, 20 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(7);
        cell.setCellValue("미션 시작일시");
        sheet.setColumnWidth(7, 20 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(8);
        cell.setCellValue("미션 종료일시");
        sheet.setColumnWidth(8, 20 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(9);
        cell.setCellValue("데일리캡 시작일");
        sheet.setColumnWidth(9, 20 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(10);
        cell.setCellValue("데일리캡 종료일");
        sheet.setColumnWidth(10, 20 * 256);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(11);
        cell.setCellValue("미션 사용여부");
        sheet.setColumnWidth(11, 16 * 256); //8자
        cell.setCellStyle(cellStyle);
        cell = row.createCell(12);
        cell.setCellValue("미션 노출여부");
        sheet.setColumnWidth(12, 16 * 256); //8자
        cell.setCellStyle(cellStyle);
        cell = row.createCell(13);
        cell.setCellValue("중복참여");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(14);
        cell.setCellValue("재참여 가능일");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(14, 20 * 256);


        row = sheet.createRow(rowNum++);

        cell = row.createCell(0);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("100");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(2);
        cell.setCellValue("5");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(3);
        cell.setCellValue("시크릿 K");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(4);
        cell.setCellValue("-");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(5);
        cell.setCellValue("title");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(6);
        cell.setCellValue("answer");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(7);
        cell.setCellStyle(cellStyle);

        cell.setCellValue("2024-01-01 00:00:00");
        cell = row.createCell(8);

        cell.setCellStyle(cellStyle);
        cell.setCellValue("2024-01-01 00:00:00");

        cell = row.createCell(9);
        cell.setCellValue("2024-01-01 00:00:00");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(10);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("2024-01-01 00:00:00");

        cell = row.createCell(11);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("활성");

        cell = row.createCell(12);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("비노출");

        cell = row.createCell(13);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("중복 허용");

        cell = row.createCell(14);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("5");

        return sheet;
    }


}





