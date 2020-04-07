package com.heartsuit.springbootsecuritydb.dto;

import lombok.Data;
@Data
public class PermissionDto {
	private Integer id;
	private String code;
	private String description;
	private String url;
}
