# Фрагменты
Что хотим увидеть:
1. 3 таба.
2. В каждом – свой бэкстэк.
3. Всё должно быть на фрагментах (с одной активити).
4. Для горизонтальной ориентации другая верстка (по ссылке – просто пример, можете сделать что-то другое). 
5. Стейт при повороте не должен теряться.
6. Для табов используем BottomNavigationView (бан за всё остальное)
7. Условие из первого бонуса обязательно для базовой версии (всё то, что после двоеточия) (первый бонус только про одинаковую работу кнопок)
8. Не забудьте про сохранение состояний, без этого ДЗ приниматься не будет
9. Для выполнения ДЗ можно пользоваться любым способом взаимодействия с фрагментами

[Как примерно должно выглядеть](https://github.com/gerra/ITMO-Android-19/blob/master/Navigation/2019-10-14_01-12-49_CaptureRecorder.mp4)

### Бонусы по 0.5
1. Кнопка “Назад” в тулбаре и системная “Назад” ведут себя одинаково: если в стеке текущего таба есть фрагменты, то выкидываем один. Иначе – или выход из приложения, или возврат на предыдущий таб с непустым стеком
2. Анимации (что-то посложнее fade in и fade out)