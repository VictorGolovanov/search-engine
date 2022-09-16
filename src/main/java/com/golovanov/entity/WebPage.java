package com.golovanov.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "page", indexes = @Index(name = "path_index", columnList = "path"))
@Getter
@Setter
public class WebPage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    @Column(name = "path", nullable = false, columnDefinition = "text")
    private String path;

    @Column(name = "code", nullable = false)
    private Integer code;

    @Column(name = "content", nullable = false, columnDefinition = "mediumtext")
    private String content;
}
