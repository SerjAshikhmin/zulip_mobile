**Тест маппинга объектов**

Написать 4 теста для одного из мапперов объектов. 

Например для маппинга сообщений чата из модели DTO в доменную модель.
маппинг полей сообщения
заполнение признака “сообщение текущего пользователя”
пустой список реакций
заполненный список реакций (любой кейс)

Если вы сделаете правильно, время займет только написание первого теста. Остальные создаются копированием.

```
data class Message(
   @SerializedName("id")
   val id: Int,
   @SerializedName("timestamp")
   val date: Long,
   @SerializedName("is_me_message")
   val isMeMessage: Boolean,
   @SerializedName("sender_id")
   val userId: Int,
   @SerializedName("sender_full_name")
   val userName: String,
   @SerializedName("content")
   val messageText: String,
   @SerializedName("avatar_url")
   val avatarUrl: String?,
   @SerializedName("reactions")
   val listReaction: List<Reaction>
)
```


```
@Entity(tableName = "message")
data class Message(
   @PrimaryKey(autoGenerate = false)
   @ColumnInfo(name = "id")
   val id: Int,
   @ColumnInfo(name = "date")
   val date: Long,
   @ColumnInfo(name = "stream_id")
   val streamId: Int,
   @ColumnInfo(name = "topic_name")
   val topicName: String,
   @ColumnInfo(name = "is_my_message")
   val isMyMessage: Boolean,
   @ColumnInfo(name = "user_id")
   val userId: Int,
   @ColumnInfo(name = "user_name")
   val userName: String,
   @ColumnInfo(name = "message_text")
   val messageText: String,
   @ColumnInfo(name = "avatar")
   val avatar: String?,
   @ColumnInfo(name = "date_for_header")
   val dateForHeader: String
)
```


**Тест репозитория**

Написать один тест репозитория. Любой репозиторий, метод на ваш выбор. 

_Требования:_

У репозитория должна быть хотя бы одна внешняя зависимость. 
Заменить реальные зависимости реализациями для тестов (моки или стабы).
Получить результат из Rx цепочки.

**Тест ViewModel**

Написать один тест для вью модели. Любая вью модель, метод на ваш выбор. 

_Требования: _

У тестируемого объекта должна быть хотя бы одна внешняя зависимость.
Заменить реальные зависимости реализациями для тестов (моки или стабы).
Получить результат из LiveData.

Если в приложение используется UDF архитектура, написать тест для Actor (или его аналог). Задача совпадает с тестом репозитория.

**UI тест**

Написать UI тест для отображения списка топиков на стартовом экране. Использовать реальный API.

Сценарий:
- Запустить приложение
- проверить, что список скрыт
- Кликнуть по кнопке 
- Убедиться, что открылся список.
- Проверить количество элементов
- Проверить тест на одном из элементов

**На всякий случай напоминаю**:
- Чистота оформления build.gradle-файла (не добавляйте лишние зависимости, удаляйте ненужные)
- Выносите версии зависимостей в ext так, как это показано [тут](https://github.com/android/architecture-samples/blob/master/build.gradle)
- Удалите папки test & androidTest – пока у вас нет тестов, эти папки вам не нужны
- Следите за чистотой кода, старайтесь избегать констант, состоящих из одной буквы и осмысленно называйте переменные
- Для домашки необходимо форкнуть мастер и создать ветку hw_7. По завершении необходимо hw_7 направить на master и сделать merge request

