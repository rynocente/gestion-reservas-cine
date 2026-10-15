CREATE TABLE movies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    director VARCHAR(255) NOT NULL,
    genre VARCHAR(50) NOT NULL,
    duration_min INT,
    rating VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE TABLE screenings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    movie_id UUID NOT NULL,
    room VARCHAR(50),
    show_at TIMESTAMP,
    total_seats INT,
    available_seats INT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_movie FOREIGN KEY (movie_id) REFERENCES movies(id)
);
