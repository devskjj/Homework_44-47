<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
    <title>Профиль</title>
</head>
<body>

<nav class="navbar navbar-expand-lg bg-body-tertiary">
    <div class="container-fluid">
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarTogglerDemo01"
                aria-controls="navbarTogglerDemo01" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarTogglerDemo01">
            <a class="navbar-brand" href="#">Офисный Booklender</a>
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="/index.html">Главная</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link"  href="/books">Книги</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/users">Сотрудники</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="/profile">Профиль</a>
                </li>
            </ul>
            <form class="d-flex" role="search">
                <button class="btn btn-outline-success me-2" type="submit">Найти</button>
                <input class="form-control me-2" type="search" placeholder="Поиск" aria-label="Search"/>
                <#if user?? && user.authorized>
                <a href="/logout" class="btn btn-outline-danger">Выход</a></form>
                </#if>
        </div>
    </div>
</nav>

<#if user??><h1 class="alert alert-secondary text-center" role="alert">
    Добро пожаловать, ${user.firstName}!
</h1><#else>
<h1 class="alert alert-secondary text-center" role="alert">
    Ваш профиль:
</h1></#if>



<div class="container text-center mb-0" style="width: 18rem;">
    <#if success?? && success>
    <div class="alert  text-bg-success w-100 mb-0 p-2" role="alert">
        <div class="d-flex">
            <div class="container text-center">
                Вы залогинились как:
            </div>
        </div>
    </div>
</#if>
    <div class="card-body p-0 mb-3">
        <div class="card" style="w-auto">
            <ul class="list-group list-group-flush">
                <#if user??>
                <li class="list-group-item">Имя: ${user.firstName!"-"}</li>
                <li class="list-group-item">Email: ${user.email!"-"}</li>
                <li class="list-group-item">Пароль: ${user.password!"-"}</li>
                <li class="list-group-item">UserId: ${user.id!"-"}</li>
                <#else>
                <li class="list-group-item">Имя: Некий пользователь</li>
                <li class="list-group-item">Email: some@email.com</li>
                <li class="list-group-item">Пароль: password</li>
                <li class="list-group-item">UserId: Некий идентификатор</li>
            </#if>
            </ul>
        </div>
    </div>
<#if success?? && success>
<div class="card-body p-0">
    <div class="card" style="w-auto">
        <ul class="list-group list-group-flush">
            <p class="alert alert-secondary mb-0 fw-bold"> История по книгам:</p>
            <#list records as record>
            <#if user.id == record.userId>
            <li class="list-group-item <#if record.returnDate??>bg-success<#else>bg-danger</#if>">
                ${record.returnDate?? ?string("Сдал", "Забрал")}
            </li>
            <li class="list-group-item">Название книги: ${books[record.bookId - 1].name}</li>
            <li class="list-group-item">Дата взятия: ${record.takeDate}</li>
            <li class="list-group-item">Дата возврата: ${record.returnDate!"-"}</li>
            </#if>
            </#list>
        </ul>
    </div>
</div>
</#if>

</div>

<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r"
        crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.min.js"
        integrity="sha384-G/EV+4j2dNv+tEPo3++6LCgdCROaejBqfUeNjuKAiuXbjrxilcCdDz6ZAVfHWe1Y"
        crossorigin="anonymous"></script>
<script>
 document.addEventListener("DOMContentLoaded", function () {
    const toastElList = document.querySelectorAll('.toast');
    const toastList = [...toastElList].map(toastEl => new bootstrap.Toast(toastEl, { autohide: false, delay: 3000 }));
    toastList.forEach(toast => toast.show());
  });
</script>
</body>
</html>