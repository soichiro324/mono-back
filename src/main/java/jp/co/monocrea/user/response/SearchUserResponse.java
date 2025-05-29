package jp.co.monocrea.user.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import jp.co.monocrea.user.entity.User;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SearchUserResponse {
    List<User> users;

    long allUsersCount;

    public SearchUserResponse(List<User> users, long allUsersCount) {
        this.users = users;
        this.allUsersCount = allUsersCount;
    }
}
