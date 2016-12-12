# Вкл/Откл защиту
ProtectionEnabled = true

# Режимы защиты.
#   BOT             => DISCONNECT   // L2Shadow, 4BOT, Adrenaline, L2Walker и др.
#   RADAR           => DISCONNECT   // l2control и др.
#   PACKET_HACK     => BAN          // l2phx, hlapex и др.

# Действия по умолчанию описаны выше.
# Для замены одного или нескольких действий на свои, используйте поле ниже.

# Возможные значения: BAN, DISCONNECT, LOG, TEMP_BAN, DELAYED_BAN
#   BAN                 - бессрочная блокировка аккаунта
#   DISCONNECT          - разрыв соединения при входе в игру
#   LOG                 - только логирование
#   TEMPORARY_BAN       - временная блокировка аккаунта:        TEMPORARY_BAN(360)          - аккаунт будет заблокирован на 360 минут
#   DELAYED_BAN         - бессрочный отложенный бан:            DELAYED_BAN(30-60)          - аккаунт будет заблокирован через 30 - 60 минут

# Пример: DetectActions = PACKET_HACK=DISCONNECT;RADAR=LOG
DetectActions = BOT=TEMPORARY_BAN(60)

# Если включено, то при блокировке HWID все аккаунты этого компьютера, находящиеся в игре, будут добавлены в банлист.
# Попытки авторизоваться на эти аккаунты будут отклонены. (Внимание! Эта опция не взаимодействует с вашей сборкой. Аккаунты блокируются только в рамках защиты.)
BanlistAccountBan = true
# Если включено, то при попытке авторизации с заблокированного HWID и НЕ заблокированного аккаунта, этот аккаунт так же будет добавлен в банлист.
BanlistAccountAppend = true

# Маска блокировки по HWID
# По умолчанию: HDD|MAC|CPU
BanMask = HDD | MAC | CPU

# Разрешить запуск игры в виртуальной среде?
# (VMWare, VirtualBox и др.)
# По умолчанию: true
AllowVirtualization = true

# Разрешить вход в игру ТОЛЬКО через запуск апдейтера?
# (Работает с LameUpdater)
OnlyUpdaterRun = false

# Максимальное количество игровых сессий с одного ПК
# По умолчанию: 0 - отключено
MaxInstances = 0

# Использовать систему версий игрового патча?
# Игровой патч должен быть настроен утилитой [PatchConfig] из комплекта защиты.
PatchVersionEnabled = false
# Минимальная версия патча, с которой игроков будет пускать на сервер.
PatchVersionMin = 1

# Управление модулями
# Возможные значения: CopyPaste, AntiClick, D3DXHook, InputFilter
# Пример: ModulesState = CopyPaste=true;AntiClick=true;D3DXHook=true;InputFilter=true (Значения по умолчанию)
ModulesState =

# Логирование авторизаций в базу в реальном времени используя пул соединений геймсервера
# (таблица auth_log будет создана автоматически)
# По умолчанию: true
LogToDatabase = true
# Логирование авторизаций в файл логов
LogToFile = false