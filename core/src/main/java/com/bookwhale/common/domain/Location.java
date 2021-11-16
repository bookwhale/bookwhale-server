package com.bookwhale.common.domain;

public enum Location {
  SEOUL("서울", "서울시"),
  BUSAN("부산", "부산시"),
  DAEGU("대구", "대구시"),
  INCHEON("인천", "인천시"),
  GWANGJU("광주", "광주시"),
  DAEJEON("대전", "대전시"),
  ULSAN("울산", "울산시"),
  SEJONG("세종", "세종시"),
  GYEONGGI("경기", "경기도"),
  GANGWON("강원", "강원도"),
  CHUNGBUK("충북", "충청북도"),
  CHUNGNAM("충남", "충청남도"),
  JEONBUK("전북", "전라북도"),
  JEONNAM("전남", "전라남도"),
  GYEONGBUK("경북", "경상북도"),
  GYEONGNAM("경남", "경상남도"),
  JEJU("제주", "제주도");


  private final String name;
  private final String detailName;

  Location(String name, String detailName) {
    this.name = name;
    this.detailName = detailName;
  }

  public String getCode() {
    return name();
  }

  public String getName() {
    return name;
  }

  public String getDetailName() {
    return detailName;
  }
}
