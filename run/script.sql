create table public.contacorrente
(
    id     bigint not null primary key,
    limite bigint not null,
    saldo  bigint not null,
    nome   varchar(255)
);

create table public.movimento
(
    datamovimento timestamp(6),
    id            bigint not null primary key,
    idcliente     bigint not null,
    valor         bigint not null,
    descricao     varchar(255),
    tipo          varchar(255)
);

create sequence public.contacorrente_seq increment by 1;
create sequence public.movimento_seq increment by 50;

alter table public.movimento owner to rinha;
alter table public.contacorrente owner to rinha;
alter sequence public.contacorrente_seq owner to rinha;
alter sequence public.movimento_seq owner to rinha;

INSERT INTO public.contacorrente (id, nome, saldo, limite)
VALUES (1, 'odorico paraguaçu', 0, 100000),
       (2, 'irmas cajazeira', 0, 80000),
       (3, 'coronel nepomuceno', 0, 1000000),
       (4, 'dirceu borboleta', 0, 10000000),
       (5, 'zeca diabo', 0, 500000);
