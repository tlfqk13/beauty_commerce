package com.example.sampleroad.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "VERSION")
@AllArgsConstructor
public class Version {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VERSION_ID")
    private Long id;

    @Column(name = "OS")
    private String os;

    @Column(name = "VERSION_NAME")
    private String versionName;

    @Column(name = "IS_REQUIRED_UPDATE")
    private boolean isRequiredUpdate;

    @Column(name = "IS_NOTIFY_UPDATE")
    private boolean isNotifyUpdate;

    public boolean getIsRequiredUpdate() {
        return isRequiredUpdate;
    }

    public boolean getIsNotifyUpdate() {
        return isNotifyUpdate;
    }
}
