Kina Studio

Sistema web de gestión para Kina Studio, un estudio de estética. Centraliza la agenda de turnos, el registro de clientas y sus servicios, los pagos y señas, el cálculo de comisiones y sueldos, el control de stock de materiales y los reportes del negocio.

Proyecto final de la carrera Analista Programador, Universidad ORT Uruguay.

Equipo
Emilia García
Alina Reyes
Tecnologías
Capa	Tecnología
Back-end	Java con Spring Boot (API REST)
Front-end	React
Base de datos	Relacional (a definir: PostgreSQL o MySQL)
Integraciones	Mercado Pago (pago de la seña), servicio de mail (confirmaciones y recordatorios)
Hosting	Azure
Gestión	GitHub (código) y Jira (tareas y horas)
Estructura del repositorio
kinaStudio-springboot/
├── backend/     API REST en Spring Boot (un paquete por módulo)
├── frontend/    Aplicación web en React
├── docs/        Documentación del proyecto
├── README.md
└── CONVENCIONES.md

Las carpetas backend/ y frontend/ se crean cuando se genere cada proyecto.

Cómo levantar el proyecto

Pendiente: se completa cuando exista el proyecto base.

Requisitos previstos: Java (versión a definir), Node.js, y la base de datos elegida.

Cómo trabajamos

Las reglas de nombres, commits, ramas y Pull Requests están en CONVENCIONES.md. Resumen:

main siempre está estable y no se sube código directo.
Cada funcionalidad se desarrolla en una rama propia (feature/rf14-agendar-turno).
Todo cambio llega a main mediante un Pull Request revisado por la otra integrante.
Cada commit indica el requerimiento que cubre (RF.14: valida solapamiento de turnos).
Estado

En desarrollo. Sprint actual: ver tablero de Jira.
