set foreign_key_checks = 0;

delete
from genre;
delete
from entertain_work;
delete
from entertain_work_genres;
delete
from plan;
delete
from user;
delete
from movie;
delete
from series;
delete
from season;
delete
FROM episode;

set foreign_key_checks = 1;

alter table genre
    auto_increment = 1;
alter table entertain_work
    auto_increment = 1;
alter table entertain_work_genres
    auto_increment = 1;
alter table plan
    auto_increment = 1;
alter table user
    auto_increment = 1;
alter table movie
    auto_increment = 1;
alter table series
    auto_increment = 1;
alter table season
    auto_increment = 1;
alter table episode
    auto_increment = 1;


insert into plan(id, name, num_credits, value, description)
values (1, 'BASIC', 1, 3.00, 'Basic plan, access to 1 movie/series per day'),
       (2, 'MEDIUM', 3, 5.00, 'Medium plan, access to access 3 movie/series per day'),
       (3, 'PREMIUM', null, 15.00, 'Premium plan, access to unlimited movies/series');

insert into genre (id, name)
values (1, 'Terror'),
       (2, 'Comedy'),
       (3, 'Action'),
       (4, 'Drama'),
       (5, 'Science Fiction'),
       (6, 'Fantasy'),
       (7, 'Horror');

INSERT INTO entertain_work (id, name, release_year, relevance, parental_rating, synopsis, type)
VALUES (1, 'The Matrix', 1999, 9, 'R', 'A computer programmer discovers a new reality.', 'MOVIE'),
       (2, 'Stranger Things', 2016, 10, 'PG_13', 'A group of kids encounters supernatural events.', 'SERIES'),
       (3, 'The Lion King', 1994, 8, 'G', 'A young lion grows up to become king.', 'MOVIE'),
       (4, 'Breaking Bad', 2008, 9, 'R', 'A chemistry teacher turns to a life of crime.', 'SERIES'),
       (5, 'Inception', 2010, 9, 'PG_13', 'A thief enters people\'s dreams to steal secrets.', 'MOVIE'),
       (6, 'Friends', 1994, 7, 'PG', 'A group of friends living in New York City.', 'SERIES'),
       (7, 'Avatar', 2009, 8, 'PG_13', 'A paraplegic marine becomes part of an alien world.', 'MOVIE'),
       (8, 'Game of Thrones', 2011, 10, 'R', 'Power struggles in the Seven Kingdoms.', 'SERIES'),
       (9, 'Toy Story', 1995, 8, 'G', 'Toys come to life when humans are not around.', 'MOVIE'),
       (10, 'The Office', 2005, 7, 'PG', 'Daily life at the Dunder Mifflin paper company.', 'SERIES');

INSERT INTO entertain_work_genres (entertain_work_id, genre_id)
VALUES (1, 5),
       (2, 7),
       (2, 6),
       (3, 4),
       (4, 4),
       (4, 3),
       (5, 5),
       (6, 2),
       (7, 5),
       (7, 3),
       (8, 3),
       (8, 6),
       (9, 2),
       (10, 2);


insert into movie(id, duration)
VALUES (1, 120),
       (3, 90),
       (5, 90),
       (7, 180),
       (9, 150);

insert into series(id)
VALUES (2),
       (4),
       (6),
       (8),
       (10);

INSERT INTO season (id, number, series, synopsis)
VALUES (1, 1, 2, 'The disappearance of a young boy triggers supernatural events in a small town.'),
       (2, 2, 2, 'The group faces new otherworldly challenges as they try to uncover the secrets of the Upside Down.'),
       (3, 1, 4,
        'A high school chemistry teacher turns to cooking and selling methamphetamine after a cancer diagnosis.'),
       (4, 2, 4, 'The consequences of Walter White\'s actions intensify as he delves deeper into the drug trade.'),
       (5, 1, 6,
        'The friends navigate the ups and downs of relationships, careers, and hilarious misadventures in New York City.'),
       (6, 2, 6,
        'As the group faces new challenges, they continue to support each other through the highs and lows of life.'),
       (7, 1, 8,
        'Noble families vie for control of the Seven Kingdoms in this epic tale of power, betrayal, and dragons.'),
       (8, 2, 8, 'The struggle for the Iron Throne intensifies as winter approaches and ancient threats re-emerge.'),
       (9, 1, 10,
        'The quirky employees of Dunder Mifflin navigate the challenges of office life, led by their bumbling boss Michael Scott.'),
       (10, 2, 10,
        'New relationships, pranks, and absurdities continue to unfold in the daily operations of the Dunder Mifflin paper company.'),
       (11, 3, 2, 'The Hawkins kids face a new threat as they encounter a mysterious girl with psychic abilities.'),
       (12, 4, 2,
        'As the Upside Down continues to influence Hawkins, the group discovers dark government experiments.'),
       (13, 3, 6,
        'Ross deals with the aftermath of his wedding, while the rest of the group navigates new challenges and adventures.');

INSERT INTO episode(id, number, name, synopsis, duration, season)
VALUES (1, 1, 'The Vanishing of Will Byers', 'A young boy disappears, and his friends embark on a quest to find him.', 50, 1),
       (2, 2, 'The Weirdo on Maple Street', 'The group intensifies their search while encountering a mysterious girl with supernatural abilities.', 48, 1),
       (3, 1, 'Madmax', 'As the gang adjusts to normal life, a new girl in town and a sinister threat emerge.', 52, 2),
       (4, 2, 'Trick or Treat, Freak', 'The kids celebrate Halloween, but strange occurrences hint at the return of the supernatural.', 47, 2),
       (5, 1, 'Pilot', 'Walter White, a high school chemistry teacher, enters the world of methamphetamine production.', 58, 3),
       (6, 2, 'Cat\'s in the Bag...', 'Walter and Jesse first meth deal goes awry, leading to unforeseen consequences.', 48, 3),
       (7, 1, 'Seven Thirty-Seven', 'Walter and Jesse face escalating threats as they try to secure their future in the drug trade.', 47, 4),
       (8, 2, 'Grilled', 'Walter and Jesse find themselves in a life-or-death situation as they cross paths with a dangerous drug lord.', 48, 4),
       (9, 1, 'The One Where It All Begins', 'The friends embark on new journeys in their personal and professional lives.', 22, 5),
       (10, 2, 'The One with the Prom Video', 'A nostalgic look back at the prom brings unexpected revelations for the group.', 22, 5),
       (11, 1, 'The One with the List', 'Relationships are tested as the group faces dilemmas and navigates through love and friendship.', 22, 6),
       (12, 2, 'The One Where Old Yeller Dies', 'Challenges and laughter ensue as the friends support each other through various life events.', 22, 6),
       (13, 1, 'Winter is Coming', 'The noble families of Westeros prepare for a power struggle as ominous signs of winter approach.', 60, 7),
       (14, 2, 'The Kingsroad', 'Amidst political intrigue and family dynamics, characters embark on separate journeys that shape their destinies.', 58, 7),
       (15, 1, 'The North Remembers', 'As the War of the Five Kings rages on, alliances are tested and new players enter the game.', 55, 8),
       (16, 2, 'The Night Lands', 'Secrets and threats unfold as characters grapple with their fates and the looming dangers beyond the Wall.', 53, 8),
       (17, 1, 'Pilot', 'The Dunder Mifflin employees are introduced, and Michael Scott unconventional leadership style is on display.', 22, 9),
       (18, 2, 'Diversity Day', 'A diversity training seminar leads to awkward and humorous situations for the office staff.', 21, 9),
       (19, 1, 'The Dundies', 'The annual Dundie Awards ceremony brings laughter, embarrassment, and unexpected recognition for the employees.', 22, 10),
       (20, 2, 'Sexual Harassment', 'The office deals with the aftermath of a sexual harassment seminar, leading to comedic misunderstandings.', 22, 10),
       (21, 1, 'The Case of the Missing Lifeguard', 'The Hawkins kids investigate the mysterious disappearance of a lifeguard at the local pool.', 55, 11),
       (22, 2, 'The Sauna Test', 'Strange occurrences lead the group to conduct a risky experiment to test the loyalty of a new member.', 52, 11),
       (23, 3, 'The Flayed', 'As the threat intensifies, the group uncovers a sinister plot involving possessed individuals and a secret Russian facility.', 48, 11),
       (24, 1, 'The Hellfire Club', 'The Hawkins kids join forces to confront a mysterious group with ties to the supernatural.', 50, 12),
       (25, 2, 'Dear Billy', 'A parallel story unfolds as the group faces personal challenges while battling a new supernatural threat.', 47, 12),
       (26, 3, 'The Nina Project', 'The group races against time to prevent a dangerous experiment that could have catastrophic consequences.', 54, 12),
       (27, 1, 'The One with the Jam', 'Ross copes with the aftermath of his wedding, leading to humorous situations for the entire group.', 22, 13),
       (28, 2, 'The One with the Metaphorical Tunnel', 'As the friends face new challenges, Chandler struggles with commitment issues.', 22, 13),
       (29, 3, 'The One with Frank Jr.', 'Phoebe discovers she has a half-brother, leading to unexpected family dynamics.', 22, 13),
       (30, 4, 'The One with the Flashback', 'The group reflects on their past as they encounter old memories and surprises.', 22, 13),
       (31, 5, 'The One with the Race Car Bed', 'Joey faces a dilemma involving Chandler, while Monica and Rachel deal with apartment-related issues.', 22, 13);

