package kr.entree.kotu.ui.data

enum class GameType(val gameName: String) {
    UNKNOWN("알 수 없음"),
    KKUTU_ENGLISH("영어 끄투"),
    CONNECT_ENGLISH("영어 끝말잇기"),
    KOONG_DDA_KOREAN("한국어 쿵쿵따"),
    CONNECT_KOREAN("한국어 끝말잇기"),
    WORD_QUIZ("자음퀴즈"),
    CROSS_WORD_KOREAN("한국어 십자말풀이"),
    TYPING_KOREAN("한국어 타자 대결"),
    TYPING_ENGLISH("영어 타자 대결"),
    CONNECT_ABOVE_KOREAN("한국어 앞말잇기"),
    CONNECT_ABOVE_ENGLISH("영어 앞말잇기"),
    HUN_MIN_JEONG_UM("훈민정음"),
    WORDING_KOREAN("한국어 단어 대결"),
    WORDING_ENGLISH("영어 단어 대결"),
    SOK_SOK_KOREAN("한국어 솎솎"),
    SOK_SOK_ENGLISH("영어 솎솎"),
    SCKETCH_QUIZ("그림퀴즈")
}