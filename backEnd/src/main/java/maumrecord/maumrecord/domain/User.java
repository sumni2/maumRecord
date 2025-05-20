package maumrecord.maumrecord.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Table(name = "USERS")
@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false,length = 50)
    private Long id;

    @Column(length = 30)
    private String nickName;

    @Column(nullable = false,unique = true,length = 50)
    private String email;

    @Column(nullable = false,length = 60) //BCryptPasswordEncoder 사용 시 password는 60자
    private String password;

    @Column(nullable = false)
    private LocalDateTime createdAt=LocalDateTime.now();

    @Column(name="image")
    private String image;

    @Enumerated(EnumType.STRING)
    private Role role =Role.USER;

    @Column()
    private String refreshToken="";

    @Builder
    public User(String email, String password, String nickName ){
        this.email=email;
        this.password=password;
        this.nickName=nickName;
    }

    @PrePersist
    public void prePersist(){
        if(this.createdAt==null)
            this.createdAt=LocalDateTime.now();
    }

    @Column(name = "journalCount")
    private Long count;

    @Column(name = "lastHealingProgram")
    private String lastHealingProgram; // e.g., '명상'

    @Column(name = "lastHealingDate")
    private String lastHealingDate;    // e.g., '2025-03-22'
    //todo: active 여부 기준 및 처리 결정
    @Column(name = "active")
    private boolean active=true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+this.role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }   //사용자 이메일 반환

    @Override
    public  String getPassword(){
        return password;
    }

    //계정 만료여부
    @Override
    public boolean isAccountNonExpired() {
        return !this.active;
    }
    //계정 잠금여부 - 현재 만료여부와 동일처리중
    @Override
    public boolean isAccountNonLocked() {
        return !this.active;
    }

    //비밀번호 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    //계정 활성화 여부
    @Override
    public boolean isEnabled() {
        return !this.active;
    }

    public enum Role {
        USER,
        ADMIN
    }
}

//todo: 계정만료, 비밀번호 만료, 계정 잠금, 계정활성화 여부에 대한 처리