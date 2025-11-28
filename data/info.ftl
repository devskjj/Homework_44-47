<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
    <title>Информация о книге</title>
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
                    <a class="nav-link active" aria-current="page" href="/books">Книги</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/users">Сотрудники</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/profile">Профиль</a>
                </li>
            </ul>
            <form class="d-flex" role="search">
                <button class="btn btn-outline-success me-2" type="submit">Найти</button>
                <input class="form-control me-2" type="search" placeholder="Поиск" aria-label="Search"/>
                <a href="/logout" class="btn btn-outline-danger">Выход</a></form>
        </div>
    </div>
</nav>


<h1 class="alert alert-secondary text-center" role="alert">
    Информация о книге:
</h1>

<div class="container text-center" style="width: 18rem;">
    <img src="/images/books/${book.id}.jpeg" class="card-img-top" alt="Обложка_книги_${book.name}">
    <div class="card-body p-0">
        <div class="card" style="w-auto">
            <ul class="list-group list-group-flush">
                <li class="list-group-item">Название: ${book.name}</li>
                <li class="list-group-item">Автор: ${book.author}</li>
                <li class="list-group-item">Описание: ${book.description}</li>
            </ul>
        </div>
    </div>
</div>


<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r"
        crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.min.js"
        integrity="sha384-G/EV+4j2dNv+tEPo3++6LCgdCROaejBqfUeNjuKAiuXbjrxilcCdDz6ZAVfHWe1Y"
        crossorigin="anonymous"></script>

</body>
</html>