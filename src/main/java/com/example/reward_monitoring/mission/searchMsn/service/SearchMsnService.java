package com.example.reward_monitoring.mission.searchMsn.service;



import com.example.reward_monitoring.general.advertiser.entity.Advertiser;
import com.example.reward_monitoring.general.advertiser.repository.AdvertiserRepository;
import com.example.reward_monitoring.general.userServer.entity.Server;
import com.example.reward_monitoring.general.userServer.repository.ServerRepository;

import com.example.reward_monitoring.mission.searchMsn.dto.*;
import com.example.reward_monitoring.mission.searchMsn.entity.SearchMsn;
import com.example.reward_monitoring.mission.searchMsn.repository.SearchMsnRepository;
import com.example.reward_monitoring.statistics.searchMsn.daily.entity.SearchMsnDailyStat;
import com.example.reward_monitoring.statistics.searchMsn.daily.repository.SearchMsnDailyStatRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
public class SearchMsnService {

    @Autowired
    private SearchMsnRepository searchMsnRepository;
    @Autowired
    private AdvertiserRepository advertiserRepository;
    @Autowired
    private ServerRepository serverRepository;
    @Autowired
    private SearchMsnDailyStatRepository searchMsnDailyStatRepository;

    public SearchMsn edit(int idx, SearchMsnEditDto dto) {
        SearchMsn searchMsn =searchMsnRepository.findByIdx(idx);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

        if(searchMsn==null)
            return null;
        if(dto.getMissionDefaultQty() != null)
            searchMsn.setMissionDefaultQty(dto.getMissionDefaultQty());

        if(dto.getMissionDailyCap() !=null)
            searchMsn.setMissionDailyCap(dto.getMissionDailyCap());
        if(dto.getAdvertiser() != null)
            searchMsn.setAdvertiser(advertiserRepository.findByAdvertiser_(dto.getAdvertiser()));
        if(dto.getAdvertiserDetails()!=null)
            searchMsn.setAdvertiserDetails(dto.getAdvertiserDetails());
        if(dto.getMissionTitle()!=null)
            searchMsn.setMissionTitle(dto.getMissionTitle());
        if(dto.getMissionExpOrder()!=null)
            searchMsn.setMissionExpOrder(dto.getMissionExpOrder());
        if(dto.getMissionContent()!=null && !(dto.getMissionContent().isEmpty()))
            searchMsn.setMissionContent(dto.getMissionContent());

        if(dto.getSearchKeyword()!=null)
            searchMsn.setSearchKeyword(dto.getSearchKeyword());

        if (dto.getStartAtMsnDate() != null && dto.getStartTime() != null) {
            LocalDate date = LocalDate.parse(dto.getStartAtMsnDate(), dateFormatter);
            LocalTime time = LocalTime.parse(dto.getStartTime(), timeFormatter);
            dto.setStartAtMsn(date.atTime(time).atZone(ZoneId.of("Asia/Seoul")));
            searchMsn.setStartAtMsn(dto.getStartAtMsn());
            searchMsn.setStartAtMsn(searchMsn.getStartAtMsn().plusHours(9));
        }
        if (dto.getEndAtMsnDate() != null && dto.getEndTime() != null) {

            LocalDate date = LocalDate.parse(dto.getEndAtMsnDate(), dateFormatter);
            LocalTime time = LocalTime.parse(dto.getEndTime(), timeFormatter);
            dto.setEndAtMsn(date.atTime(time).atZone(ZoneId.of("Asia/Seoul")));
            searchMsn.setEndAtMsn(dto.getEndAtMsn());
            searchMsn.setEndAtMsn(searchMsn.getEndAtMsn().plusHours(9));
        }

        if (dto.getStartAtCap() != null)
            searchMsn.setStartAtCap(dto.getStartAtCap());

        if (dto.getEndAtCap() != null)
            searchMsn.setEndAtCap(dto.getEndAtCap());

        if (dto.getMissionActive() != null) {
            boolean bool = dto.getMissionActive();
            searchMsn.setMissionActive(bool);
        }
        if (dto.getMissionExposure() != null) {
            boolean bool = dto.getMissionExposure();
            searchMsn.setMissionExposure(bool);
        }

        if (dto.getDupParticipation() != null) {
            boolean bool = dto.getDupParticipation();
            searchMsn.setDupParticipation(bool);
            if(!searchMsn.isDupParticipation())
                searchMsn.setReEngagementDay(null);
        }

        if (dto.getReEngagementDay() != null) {
            searchMsn.setReEngagementDay(dto.getReEngagementDay());
        }

        if(dto.getExceptMedia() !=null && !(dto.getExceptMedia().isEmpty())) {
            searchMsn.setExceptMedia(searchMsn.convertJsonToString(searchMsn.convertDataToJson(dto.getExceptMedia())));
        }

        if(dto.getMsnUrl()!=null)
            searchMsn.setMsnUrl(dto.getMsnUrl());

        if(dto.getMsnAnswer()!=null)
            searchMsn.setMsnAnswer(dto.getMsnAnswer());

        if(dto.getMsnAnswer2()!=null)
            searchMsn.setMsnAnswer2(dto.getMsnAnswer2());

        if(dto.getImageName()!=null && !(dto.getImageName().isEmpty())){
            searchMsn.setImageName(dto.getImageName());
        }



        return searchMsn;
    }

    public SearchMsn add(SearchMsnReadDto dto) {
        Server serverEntity = null;
        Advertiser advertiserEntity = advertiserRepository.findByAdvertiser_(dto.getAdvertiser());
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

        if (dto.getStartAtMsnDate() != null && dto.getStartTime() != null) {

            LocalDate date = LocalDate.parse(dto.getStartAtMsnDate(), dateFormatter);
            LocalTime time = LocalTime.parse(dto.getStartTime(), timeFormatter);
            dto.setStartAtMsn(ZonedDateTime.of(date.atTime(time), ZoneId.systemDefault()));
        }
        if (dto.getEndAtMsnDate() != null && dto.getEndTime() != null) {

            LocalDate date = LocalDate.parse(dto.getEndAtMsnDate(), dateFormatter);
            LocalTime time = LocalTime.parse(dto.getEndTime(), timeFormatter);
            dto.setEndAtMsn(ZonedDateTime.of(date.atTime(time), ZoneId.systemDefault()));
        }
        if(dto.getUrl()!= null)
            serverEntity = serverRepository.findByServerUrl_(dto.getUrl());
        else
            serverEntity = serverRepository.findByServerUrl_("https://ocb.srk.co.kr");//default 서버 주소

        dto.setDataType(true);
        if(!dto.getDupParticipation())
            dto.setReEngagementDay(null);
        return dto.toEntity(advertiserEntity,serverEntity);
    }

    public SearchMsn getSearchMsn(int idx) {
        return searchMsnRepository.findByIdx(idx);
    }

    public List<SearchMsn> getSearchMsns() {
        return searchMsnRepository.findAllMission();
    }

    public SearchMsn delete(int idx) {
        SearchMsn target = searchMsnRepository.findByIdx(idx);
        if(target==null)
            return null;
        searchMsnRepository.delete(target);
        return target;
    }

    public boolean hidden(int idx) {

        SearchMsn target= searchMsnRepository.findByIdx(idx);
        if(target == null)
            return false;
        target.setDataType(false);
        searchMsnRepository.save(target);
        return true;
    }

    public List<SearchMsn> searchSearchMsn(SearchMsnSearchDto dto) {

        List<SearchMsn> target_date=null;
        List<SearchMsn> target_dailyCap = null;

        List<SearchMsn> target_dup_Participation = null;
        List<SearchMsn> target_mission_active = null;
        List<SearchMsn> target_mission_exposure = null;
        List<SearchMsn> target_data_Type = null;

        List<SearchMsn> target_advertiser=null;
        List<SearchMsn> target_advertiser_details=null; // 선택 1
        List<SearchMsn> target_mission_title = null; // 선택 2


        List<SearchMsn> result;
        boolean changed = false;


        if(dto.getStartAtMsn() != null || dto.getEndAtMsn() != null){
            if(dto.getStartAtMsn() != null){

                ZoneId zoneId = ZoneId.of("Asia/Seoul");
                ZonedDateTime start_time = dto.getStartAtMsn().atStartOfDay(zoneId);
                if(dto.getEndAtMsn() == null){
                    target_date = searchMsnRepository.findByStartDate(start_time);;
                }else{
                    ZonedDateTime end_time = dto.getEndAtMsn().atStartOfDay(zoneId).plusHours(23).plusMinutes(59);
                    target_date = searchMsnRepository.findByBothDate(start_time,end_time);
                }

            }
            else {
                ZoneId zoneId = ZoneId.of("Asia/Seoul");
                ZonedDateTime end_time = dto.getEndAtMsn().atStartOfDay(zoneId).plusHours(23).plusMinutes(59);
                target_date = searchMsnRepository.findByEndDate(end_time);
            }

        }

        if(dto.getStartAtCap() != null || dto.getEndAtCap() != null){
            if(dto.getStartAtCap() != null){
                if(dto.getEndAtCap() == null){
                    target_dailyCap = searchMsnRepository.findByStartAtCap(dto.getStartAtCap());
                }else{
                    target_dailyCap = searchMsnRepository.findByBothCap(dto.getStartAtCap(),dto.getEndAtCap());
                }

            }
            else {
                target_dailyCap = searchMsnRepository.findByEndAtCap(dto.getEndAtCap());
            }

        }

        if(dto.getMissionActive() != null){
            target_mission_active = searchMsnRepository.findByMissionActive(dto.getMissionActive());
        }

        if(dto.getDupParticipation() != null){
            target_dup_Participation = searchMsnRepository.findByDupParticipation(dto.getDupParticipation());
        }
        if(dto.getMissionExposure() != null){
            target_mission_exposure = searchMsnRepository.findByMissionExposure(dto.getMissionExposure());
        }

        if(dto.getDataType() != null){
            target_data_Type= searchMsnRepository.findByDataType(dto.getDataType());
        }

        if(dto.getAdvertiser()!=null){
            target_advertiser = searchMsnRepository.findByAdvertiser(dto.getAdvertiser());
        }

        if(dto.getAdvertiserDetails()!=null){
            target_advertiser_details = searchMsnRepository.findByAdvertiserDetails(dto.getAdvertiserDetails());
        }

        if(dto.getMissionTitle()!= null) {
            target_mission_title = searchMsnRepository.findByMissionTitle(dto.getMissionTitle());
        }

        result = new ArrayList<>(searchMsnRepository.findAll());

        if(target_date != null){
            Set<Integer> idxSet = target_date.stream().map(SearchMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(searchMsn -> idxSet.contains(searchMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_dailyCap!= null){
            Set<Integer> idxSet = target_dailyCap.stream().map(SearchMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(searchMsn -> idxSet.contains(searchMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_dup_Participation!= null){
            Set<Integer> idxSet = target_dup_Participation.stream().map(SearchMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(searchMsn -> idxSet.contains(searchMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_mission_active!= null){
            Set<Integer> idxSet = target_mission_active.stream().map(SearchMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(searchMsn -> idxSet.contains(searchMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_mission_exposure!= null){
            Set<Integer> idxSet = target_mission_exposure.stream().map(SearchMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(searchMsn -> idxSet.contains(searchMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_data_Type!= null){
            Set<Integer> idxSet = target_data_Type.stream().map(SearchMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(searchMsn -> idxSet.contains(searchMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_advertiser!= null){
            Set<Integer> idxSet = target_advertiser.stream().map(SearchMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(searchMsn -> idxSet.contains(searchMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_advertiser_details!= null){
            Set<Integer> idxSet = target_advertiser_details.stream().map(SearchMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(searchMsn -> idxSet.contains(searchMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        else if(target_mission_title!= null){
            Set<Integer> idxSet = target_mission_title.stream().map(SearchMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(searchMsn -> idxSet.contains(searchMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(!changed)
            result = new ArrayList<>();
        return result;

    }
    public Sheet excelDownload(List<SearchMsn> list, Workbook wb){

        int size = list.size();
        Sheet sheet = wb.createSheet("정답 미션 목록");
        Row row;
        Cell cell;
        CellStyle cellStyle = wb.createCellStyle();
        applyCellStyle(cellStyle);
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("searchIdx");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("기본 수량");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue("데일리캡");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("광고주");
        sheet.setColumnWidth(3, 16 * 256);
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
        sheet.setColumnWidth(6, 30 * 256);

        cell = row.createCell(7);
        cell.setCellValue("검색 키워드");
        sheet.setColumnWidth(7, 40 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(8);
        cell.setCellValue("미션 시작일시");
        sheet.setColumnWidth(8, 20 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(9);
        cell.setCellValue("미션 종료일시");
        sheet.setColumnWidth(9, 20 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(10);
        cell.setCellValue("데일리캡 시작일");
        sheet.setColumnWidth(10, 20 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(11);
        cell.setCellValue("데일리캡 종료일");
        sheet.setColumnWidth(11, 20 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(12);
        cell.setCellValue("미션 사용여부");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(13);
        cell.setCellValue("미션 노출여부");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(14);
        cell.setCellValue("중복참여");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(15);
        cell.setCellValue("재참여 가능일");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(15, 20 * 256);


        for (SearchMsn searchMsn : list) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(searchMsn.getIdx());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(1);
            cell.setCellValue(searchMsn.getMissionDefaultQty());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(2);
            cell.setCellValue(searchMsn.getMissionDailyCap());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(3);
            cell.setCellValue(searchMsn.getAdvertiser().getAdvertiser());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(4);
            cell.setCellValue(searchMsn.getAdvertiserDetails());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(5);
            cell.setCellValue(searchMsn.getMissionTitle());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(6);
            cell.setCellValue(searchMsn.getMsnAnswer());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(7);
            cell.setCellValue(searchMsn.getSearchKeyword());
            cell.setCellStyle(cellStyle);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            cell = row.createCell(8);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(searchMsn.getStartAtMsn().format(formatter));

            cell = row.createCell(9);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(searchMsn.getEndAtMsn().format(formatter));

            cell = row.createCell(10);
            cell.setCellStyle(cellStyle);
            DateTimeFormatter formatter_ = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            cell.setCellValue(searchMsn.getStartAtCap().format(formatter_));

            cell = row.createCell(11);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(searchMsn.getEndAtCap().format(formatter_));


            cell = row.createCell(12);
            cell.setCellStyle(cellStyle);
            if(searchMsn.isMissionActive())
                cell.setCellValue("활성");
            else
                cell.setCellValue("비활성");

            cell = row.createCell(13);
            cell.setCellStyle(cellStyle);
            if(searchMsn.isMissionExposure())
                cell.setCellValue("노출");
            else
                cell.setCellValue("비노출");

            cell = row.createCell(14);
            cell.setCellStyle(cellStyle);
            if(searchMsn.isDupParticipation())
                cell.setCellValue("중복 허용");
            else
                cell.setCellValue("중복 불가");

            cell = row.createCell(15);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(searchMsn.getReEngagementDay());

        }
        return sheet;
    }

    private void applyCellStyle(CellStyle cellStyle) {
        XSSFCellStyle xssfCellStyle = (XSSFCellStyle) cellStyle;
//        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
    }

    @Transactional
    public boolean readExcel(MultipartFile file)throws IOException {

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
        SearchMsnReadDto dto = new SearchMsnReadDto();
        Advertiser advertiserEntity = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter_date = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for(int i = worksheet.getPhysicalNumberOfRows() - 1; i >= 1; i--) {

            Row row = worksheet.getRow(i);

            if (row.getCell(1) != null && row.getCell(1).getCellType() == CellType.NUMERIC) {
                dto.setMissionDefaultQty((int) row.getCell(1).getNumericCellValue());
            }

            if(row.getCell(2)!=null)
                dto.setMissionDailyCap((int)row.getCell(2).getNumericCellValue());
            advertiserEntity = advertiserRepository.findByAdvertiser_(row.getCell(3).getStringCellValue());

            //셀에있는 데이터를 읽어와 그걸로 repository 에서 일치하는 advertiser 를 가져온다.
            if(row.getCell(4)!=null)
                dto.setAdvertiserDetails(row.getCell(4).getStringCellValue());

            if(row.getCell(5)!=null)
                dto.setMissionTitle(row.getCell(5).getStringCellValue());

            if(row.getCell(6)!=null)
                dto.setSearchKeyword(row.getCell(6).getStringCellValue());

            if(row.getCell(7)!=null)
                dto.setSearchKeyword(row.getCell(7).getStringCellValue());

            if(row.getCell(8)!=null)
                dto.setStartAtMsn(ZonedDateTime.of(LocalDateTime.parse(row.getCell(8).getStringCellValue(),formatter),ZoneId.systemDefault()));

            if(row.getCell(9)!=null)
                dto.setEndAtMsn(ZonedDateTime.of(LocalDateTime.parse(row.getCell(9).getStringCellValue(),formatter),ZoneId.systemDefault()));

            if(row.getCell(10)!=null)
                dto.setStartAtCap(LocalDate.parse(row.getCell(10).getStringCellValue(),formatter_date));

            if(row.getCell(11)!=null)
                dto.setEndAtCap(LocalDate.parse(row.getCell(11).getStringCellValue(),formatter_date));

            if(Objects.equals(row.getCell(12).getStringCellValue(), "활성"))
                dto.setMissionActive(true);
            else
                dto.setMissionActive(false);

            if(Objects.equals(row.getCell(13).getStringCellValue(), "노출"))
                dto.setMissionExposure(true);
            else
                dto.setMissionExposure(false);

            if(Objects.equals(row.getCell(14).getStringCellValue(),"중복 허용"))
                dto.setDupParticipation(true);
            else
                dto.setDupParticipation(false);

            dto.setReEngagementDay((int)row.getCell(15).getNumericCellValue());

            searchMsnRepository.save(dto.toEntity(advertiserEntity,null));

        }
        return true;
    }

    public boolean allMissionEnd() {

        List<SearchMsn> target = getSearchMsns();
        if(target == null)
            return false;

        for(SearchMsn searchMsn : target){
            searchMsn.setMissionExposure(false);
            searchMsn.setMissionActive(false);
            searchMsnRepository.save(searchMsn);
        }
        return true;
    }


    public boolean changeAbleDay(SearchMsnAbleDayDto dto, int idx) {
        SearchMsn searchMsn = searchMsnRepository.findByIdx(idx);
        if(searchMsn == null)
            return false;

        searchMsn.setDupParticipation(dto.isDupParticipation());
        if(dto.getReEngagementDay()!=null)
            searchMsn.setReEngagementDay(dto.getReEngagementDay());
        return true;

    }

    public boolean changeMissionActive(int idx, SearchMsnActiveDto dto) {
        SearchMsn target =searchMsnRepository.findByIdx(idx);
        if(target ==null)
            return false;
        target.setMissionActive(dto.isActive());
        return true;
    }
    public boolean changeMissionExpose(int idx, SearchMsnExposeDto dto) {

        SearchMsn target =searchMsnRepository.findByIdx(idx);
        if(target ==null)
            return false;
        target.setMissionExposure(dto.isExpose());
        return true;
    }

    public boolean setMissionIsUsed(int idx) {
        SearchMsn target= searchMsnRepository.findByIdx(idx);
        if(target == null)
            return false;
        target.setMissionActive(true);
        searchMsnRepository.save(target);
        return true;
    }

    public boolean setMissionIsUsedFalse(int idx) {
        SearchMsn target= searchMsnRepository.findByIdx(idx);
        if(target == null)
            return false;
        target.setMissionActive(false);
        searchMsnRepository.save(target);
        return true;
    }


    public boolean setMissionIsView(int idx) {
        SearchMsn target= searchMsnRepository.findByIdx(idx);
        if(target == null)
            return false;
        target.setMissionExposure(true);
        searchMsnRepository.save(target);
        return true;
    }

    public boolean setMissionIsViewFalse(int idx) {
        SearchMsn target= searchMsnRepository.findByIdx(idx);
        if(target == null)
            return false;
        target.setMissionExposure(false);
        searchMsnRepository.save(target);
        return true;
    }

    public List<SearchMsn> searchSearchMsnCurrent(SearchMsnSearchByConsumedDto dto) {
        List<SearchMsn> target_advertiser = null;
        List<SearchMsn> target_serverUrl = null;
        List<SearchMsn> target_advertiser_details = null; // 선택 1
        List<SearchMsn> target_mission_title = null; // 선택 2



        List<SearchMsn> result;
        boolean changed = false;

        if(dto.getAdvertiser()!=null){
            target_advertiser = searchMsnRepository.findByAdvertiser(dto.getAdvertiser());
        }

        if(dto.getServerUrl()!=null){
            target_serverUrl = searchMsnRepository.findByServer_(dto.getServerUrl());
        }

        if(dto.getAdvertiserDetails() != null  && !dto.getAdvertiserDetails().isEmpty())
            target_advertiser_details = searchMsnRepository.findByAdvertiserDetails(dto.getAdvertiserDetails());

        if(dto.getMissionTitle() != null && !dto.getMissionTitle().isEmpty())
            target_mission_title = searchMsnRepository.findByMissionTitle(dto.getMissionTitle());

        ZonedDateTime now = ZonedDateTime.now();
        result = new ArrayList<>(searchMsnRepository.findByCurrentList(now));


        if(target_serverUrl !=null) {
            Set<Integer> idxSet = target_serverUrl.stream().map(SearchMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(searchMsn -> idxSet.contains(searchMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_advertiser != null) {
            Set<Integer> idxSet = target_advertiser.stream().map(SearchMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(searchMsn -> idxSet.contains(searchMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        if(target_advertiser_details != null) {
            Set<Integer> idxSet = target_advertiser_details.stream().map(SearchMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(searchMsn -> idxSet.contains(searchMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }
        else if(target_mission_title !=null) {
            Set<Integer> idxSet = target_mission_title.stream().map(SearchMsn::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(searchMsn -> idxSet.contains(searchMsn.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(!changed)
            result = new ArrayList<>();
        return result;
    }


    public Sheet excelDownloadCurrent( List<SearchMsn> list,Workbook wb){

        int size = list.size();
        Sheet sheet = wb.createSheet("검색 미션 목록");
        Row row = null;
        Cell cell = null;
        CellStyle cellStyle = wb.createCellStyle();
        applyCellStyle(cellStyle);
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("searchIdx");
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

        for (SearchMsn searchMsn : list) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(searchMsn.getIdx());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(1);
            cell.setCellValue(searchMsn.getAdvertiserDetails());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(2);
            cell.setCellValue(searchMsn.getMissionTitle());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(3);
            cell.setCellValue(searchMsn.getMissionDefaultQty());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(4);
            cell.setCellValue(searchMsn.getMissionDailyCap());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(5);
            cell.setCellValue(searchMsn.getTotalLandingCnt());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(6);
            cell.setCellValue(searchMsn.getTotalPartCnt());
            cell.setCellStyle(cellStyle);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            cell = row.createCell(7);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(searchMsn.getStartAtMsn().format(formatter));

            cell = row.createCell(8);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(searchMsn.getEndAtMsn().format(formatter));

            cell = row.createCell(9);
            cell.setCellStyle(cellStyle);
            DateTimeFormatter formatter_ = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            cell.setCellValue(searchMsn.getStartAtCap().format(formatter_));
            cell.setCellStyle(cellStyle);

            cell = row.createCell(10);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(searchMsn.getEndAtCap().format(formatter_));
            cell.setCellStyle(cellStyle);

            cell = row.createCell(11);
            cell.setCellStyle(cellStyle);
            if(searchMsn.isMissionActive())
                cell.setCellValue("활성");
            else
                cell.setCellValue("비활성");

            cell = row.createCell(12);
            cell.setCellStyle(cellStyle);
            if(searchMsn.isMissionExposure())
                cell.setCellValue("노출");
            else
                cell.setCellValue("비노출");

            cell = row.createCell(13);
            cell.setCellStyle(cellStyle);
            if(searchMsn.isDupParticipation())
                cell.setCellValue("중복 허용");
            else
                cell.setCellValue("중복 불가");

            cell = row.createCell(14);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(searchMsn.getReEngagementDay());
        }
        return sheet;
    }
    public boolean setOffMissionIsUsed(int idx) {

        List<SearchMsn> searchMsns = getSearchMsns();
        Collections.reverse(searchMsns);

        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (idx - 1) * limit;


        // 전체 리스트의 크기 체크
        List<SearchMsn> limitedSearchMsns;
        if (startIndex < searchMsns.size()) {
            int endIndex = Math.min(startIndex + limit, searchMsns.size());
            limitedSearchMsns = searchMsns.subList(startIndex, endIndex);
        } else {
            return false;
        }
        for (SearchMsn searchMsn : limitedSearchMsns) {
            searchMsn.setMissionActive(false); // isUsed 필드를 false로 설정
            searchMsnRepository.save(searchMsn);
        }
        return true;
    }

    public boolean setOffMissionIsUsed(int idx,List<SearchMsn> target) {



        for (SearchMsn searchMsn : target) {
            searchMsn.setMissionActive(false); // isUsed 필드를 false로 설정
            searchMsnRepository.save(searchMsn);
        }
        return true;
    }


    public boolean setOffMissionIsView(int idx) {
        List<SearchMsn> searchMsns = getSearchMsns();
        Collections.reverse(searchMsns);

        // 한 페이지당 최대 10개 데이터
        int limit = 10;
        int startIndex = (idx - 1) * limit;

        // 전체 리스트의 크기 체크
        List<SearchMsn> limitedSearchMsns;
        if (startIndex < searchMsns.size()) {
            int endIndex = Math.min(startIndex + limit, searchMsns.size());
            limitedSearchMsns = searchMsns.subList(startIndex, endIndex);
        } else {
            return false;
        }
        for (SearchMsn searchMsn : limitedSearchMsns) {
            searchMsn.setMissionExposure(false); // missionExpose 필드를 false로 설정
            searchMsnRepository.save(searchMsn);
        }
        return true;
    }

    public boolean setOffMissionIsView(int idx,List<SearchMsn> target) {


        for (SearchMsn searchMsn : target) {
            searchMsn.setMissionExposure(false); // missionExpose 필드를 false로 설정
            searchMsnRepository.save(searchMsn);
        }
        return true;
    }


    public boolean AllOffMission() {
        List<SearchMsn> searchMsns = getSearchMsns();
        for (SearchMsn searchMsn : searchMsns) {
            searchMsn.setMissionActive(false); // isUsed 필드를 false로 설정
            searchMsn.setMissionExposure(false);
            searchMsnRepository.save(searchMsn);
        }
        return true;
    }

    public boolean AllOffMissionCurrent() {
        ZonedDateTime now = ZonedDateTime.now();
        List<SearchMsn> searchMsns = searchMsnRepository.findByCurrentList(now);
        for (SearchMsn searchMsn : searchMsns) {
            searchMsn.setMissionActive(false); // isUsed 필드를 false로 설정
            searchMsn.setMissionExposure(false);
            searchMsnRepository.save(searchMsn);
        }
        return true;
    }

    public boolean changeMissionReEngagementDay(int idx, SearchMsnAbleDayDto dto) {
        SearchMsn target = searchMsnRepository.findByIdx(idx);
        if(target ==null)
            return false;
        target.setDupParticipation(dto.isDupParticipation());
        if(!dto.isDupParticipation())
            target.setReEngagementDay(null);
        else
            target.setReEngagementDay(dto.getReEngagementDay());
        searchMsnRepository.save(target);
        return true;
    }

    public Sheet reportExcelDownload(List<SearchMsnDailyStat> list, Workbook wb) {
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

        for (SearchMsnDailyStat searchMsnDailyStat : list) {

            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            DateTimeFormatter formatter_ = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            cell.setCellValue(searchMsnDailyStat.getPartDate().format(formatter_));
            cell.setCellStyle(cellStyle);

            cell = row.createCell(1);
            cell.setCellValue(searchMsnDailyStat.getLandingCnt());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(2);
            cell.setCellValue(searchMsnDailyStat.getPartCnt());
            cell.setCellStyle(cellStyle);
        }
        return sheet;
    }

    public List<SearchMsnDailyStat> searchReport(searchStaticListSearchDto dto, Integer idx) {
        List<SearchMsnDailyStat> target_url = null;
        List<SearchMsnDailyStat> target_date = null;
        List<SearchMsnDailyStat> target_idx = searchMsnDailyStatRepository.findByMsnIdx_(idx);


        List<SearchMsnDailyStat> result;
        boolean changed = false;

        if(dto.getStartDate() != null || dto.getEndDate() != null) {
            if (dto.getStartDate() != null) {
                if (dto.getEndDate() == null) {
                    target_date  = searchMsnDailyStatRepository.findByStartAt(dto.getStartDate());
                } else {
                    target_date  = searchMsnDailyStatRepository.findByBothAt(dto.getStartDate(), dto.getEndDate());
                }

            } else {
                target_date  = searchMsnDailyStatRepository.findByEndAt(dto.getEndDate());
            }
        }

        if(dto.getUrl() != null)
            target_url = searchMsnDailyStatRepository.findByServer_ServerUrl(dto.getUrl());




        result = new ArrayList<>(searchMsnDailyStatRepository.findAll());

        if(target_idx  !=null) {
            Set<Integer> idxSet = target_idx.stream().map(SearchMsnDailyStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(searchMsnDailyStat -> idxSet.contains(searchMsnDailyStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }


        if(target_date  !=null) {
            Set<Integer> idxSet = target_date.stream().map(SearchMsnDailyStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(searchMsnDailyStat -> idxSet.contains(searchMsnDailyStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }

        if(target_url  !=null) {
            Set<Integer> idxSet = target_url.stream().map(SearchMsnDailyStat::getIdx).collect(Collectors.toSet());
            result = result.stream().filter(searchMsnDailyStat -> idxSet.contains(searchMsnDailyStat.getIdx())).distinct().collect(Collectors.toList());
            changed = true;
        }


        if(!changed)
            result = new ArrayList<>();

        return result;
    }

    public Sheet downloadSearchForm(Workbook wb) {
        Sheet sheet = wb.createSheet("정답 미션 목록");
        Row row;
        Cell cell;
        CellStyle cellStyle = wb.createCellStyle();
        applyCellStyle(cellStyle);
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("searchIdx(공란)");
        sheet.setColumnWidth(0, 16 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("기본 수량");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue("데일리캡");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("광고주");
        sheet.setColumnWidth(3, 16 * 256);
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
        sheet.setColumnWidth(6, 30 * 256);

        cell = row.createCell(7);
        cell.setCellValue("검색 키워드");
        sheet.setColumnWidth(7, 40 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(8);
        cell.setCellValue("미션 시작일시");
        sheet.setColumnWidth(8, 20 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(9);
        cell.setCellValue("미션 종료일시");
        sheet.setColumnWidth(9, 20 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(10);
        cell.setCellValue("데일리캡 시작일");
        sheet.setColumnWidth(10, 20 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(11);
        cell.setCellValue("데일리캡 종료일");
        sheet.setColumnWidth(11, 20 * 256);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(12);
        cell.setCellValue("미션 사용여부");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(13);
        cell.setCellValue("미션 노출여부");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(14);
        cell.setCellValue("중복참여");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(15);
        cell.setCellValue("재참여 가능일");
        cell.setCellStyle(cellStyle);
        sheet.setColumnWidth(15, 20 * 256);




        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("100");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue("10");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("시크릿 K");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("0");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(5);
        cell.setCellValue("title");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(6);
        cell.setCellValue("answer");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(7);
        cell.setCellValue("keyword");
        cell.setCellStyle(cellStyle);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        cell = row.createCell(8);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("2024-01-01 00:00:00");

        cell = row.createCell(9);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("2024-01-01 00:00:00");

        cell = row.createCell(10);
        cell.setCellStyle(cellStyle);
        DateTimeFormatter formatter_ = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        cell.setCellValue("2024-01-01");

        cell = row.createCell(11);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("2024-01-01");


        cell = row.createCell(12);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("활성/비활성");


        cell = row.createCell(13);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("노출/비노출");


        cell = row.createCell(14);
        cell.setCellStyle(cellStyle);
       cell.setCellValue("중복 허용/중복 불가");


        cell = row.createCell(15);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("5");


        return sheet;
    }
}
