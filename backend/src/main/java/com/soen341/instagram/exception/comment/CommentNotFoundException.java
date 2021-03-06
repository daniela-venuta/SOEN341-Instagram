package com.soen341.instagram.exception.comment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CommentNotFoundException extends RuntimeException
{
	public CommentNotFoundException()
	{
		super("Comment not found");
	}

	public CommentNotFoundException(String msg)
	{
		super(msg);
	}
}
