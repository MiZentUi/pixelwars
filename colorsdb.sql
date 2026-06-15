--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: color; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.color (
    id bigint NOT NULL,
    color character varying(255)
);


ALTER TABLE public.color OWNER TO root;

--
-- Name: color_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.color_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.color_seq OWNER TO root;

--
-- Name: pixel; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.pixel (
    id bigint NOT NULL,
    color character varying(255),
    "timestamp" bigint,
    x integer,
    y integer,
    user_id bigint NOT NULL
);


ALTER TABLE public.pixel OWNER TO root;

--
-- Name: pixel_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.pixel_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pixel_seq OWNER TO root;

--
-- Name: users; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    email character varying(255),
    name character varying(255),
    picture character varying(255),
    status character varying(255),
    subject character varying(255)
);


ALTER TABLE public.users OWNER TO root;

--
-- Name: users_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.users_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_seq OWNER TO root;

--
-- Data for Name: color; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.color (id, color) FROM stdin;
1	#6d001a
2	#be0039
3	#ff4500
4	#ffa800
5	#ffd635
6	#fff8b8
7	#00a368
8	#00cc78
9	#7eed56
10	#00756f
11	#009eaa
12	#00ccc0
13	#2450a4
14	#3690ea
15	#51e9f4
16	#493ac1
17	#6a5cff
18	#94b3ff
19	#811e9f
20	#b44ac0
21	#e4abff
22	#de107f
23	#ff3881
24	#ff99aa
25	#6d482f
26	#9c6926
27	#ffb470
28	#000000
29	#515252
30	#898d90
31	#d4d7d9
32	#ffffff
\.


--
-- Data for Name: pixel; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.pixel (id, color, "timestamp", x, y, user_id) FROM stdin;
1	rgb(109, 0, 26)	1746219958479	146	122	1
2	rgb(109, 0, 26)	1746220480546	129	138	1
52	rgb(109, 0, 26)	1746220577399	104	145	1
53	rgb(109, 0, 26)	1746220591536	68	147	1
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.users (id, email, name, picture, status, subject) FROM stdin;
1	mizentui@gmail.com	Денис Скробот	https://lh3.googleusercontent.com/a/ACg8ocLwg-KsLrFRKlHrSc6fw1ySkESQHvtGcayuGy6fvnIulpMQM4kp=s96-c	owner	104162136125914744512
\.


--
-- Name: color_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.color_seq', 1, false);


--
-- Name: pixel_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.pixel_seq', 101, true);


--
-- Name: users_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.users_seq', 1, true);


--
-- Name: color color_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.color
    ADD CONSTRAINT color_pkey PRIMARY KEY (id);


--
-- Name: pixel pixel_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.pixel
    ADD CONSTRAINT pixel_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: pixel fkrujxdjtv4m312621ndjnf0hk7; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.pixel
    ADD CONSTRAINT fkrujxdjtv4m312621ndjnf0hk7 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- PostgreSQL database dump complete
--

