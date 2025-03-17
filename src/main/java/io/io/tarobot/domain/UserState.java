package io.io.tarobot.domain;

public enum UserState {
    CHOOSING_CATEGORY,  // Выбор темы
    ENTERING_QUESTION,  // Ввод вопроса
    WAITING_FOR_CONFIRMATION,  // Ожидание нажатия "Сделать расклад"
    AWAITING_CLARIFICATION,  // Уточняющий вопрос после расклада
    IDLE  // Без активного состояния
}
