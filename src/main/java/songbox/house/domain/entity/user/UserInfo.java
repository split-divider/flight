package songbox.house.domain.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import songbox.house.domain.entity.YoutubePlaylist;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static javax.persistence.CascadeType.ALL;

@Table
@Entity
@Getter
@Setter
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column
    @JsonIgnore
    private String password;

    @Column
    private String name;

    @Column
    private String surname;

    @Column
    private String email;

    @Column
    private Boolean active;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    @Column
    private Date createDate;

    @OneToOne
    private UserRole role;

    @OneToMany(
            mappedBy = "owner",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<YoutubePlaylist> youtubePlaylists = new ArrayList<>();

    @OneToOne(cascade = ALL)
    private UserProperty userProperty;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return Objects.equals(userId, userInfo.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
