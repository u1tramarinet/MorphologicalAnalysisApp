package com.u1tramarinet.twitter_api.model;

import java.util.Objects;

public class Tweet implements Identifiable {
    public final long id;
    public final String text;

    public Tweet(long id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tweet tweet = (Tweet) o;
        return id == tweet.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
