package com.soen341.instagram.controller;

import java.util.List;

// Spring Boot
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

//Project
import com.soen341.instagram.dao.impl.AccountRepository;
import com.soen341.instagram.dto.picture.PictureDTO;
import com.soen341.instagram.exception.account.AccountNotFoundException;
import com.soen341.instagram.model.Account;
import com.soen341.instagram.model.Picture;
import com.soen341.instagram.service.impl.PictureService;
import com.soen341.instagram.utils.UserAccessor;

@RestController
public class PictureController
{
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private PictureService pictureService;

	@PostMapping(value = "/picture", consumes = { "multipart/form-data" })
	@ResponseStatus(value = HttpStatus.CREATED)
	// Can't use RequestBody with multi-part, so have to use RequestParam
	public PictureDTO uploadPicture(@RequestParam(required = false) String caption, @RequestParam MultipartFile picture)
	{
		Picture newPic = pictureService.uploadPicture(caption, picture, UserAccessor.getCurrentAccount(accountRepository));
		
		return pictureService.toPictureDTO(newPic);
	}

	@GetMapping(value = "/picture/feed")
	public List<Long> getFeed(@RequestParam(defaultValue = "10") int count, @RequestParam(defaultValue = "0") long after)
	{
		return pictureService.getFeed(count, after, UserAccessor.getCurrentAccount(accountRepository));
	}

	@GetMapping(value = "/picture/{id}")
	public PictureDTO getPicture(@PathVariable String id)
	{
		return pictureService.getPictureDTOFromId(id);
	}

	@GetMapping(value = "/picture/{id}.jpg", produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getPictureFile(@PathVariable String id)
	{
		return pictureService.loadPicture(id);
	}

	@GetMapping(value = "/{username}/pictures")
	public List<Long> getAccountPictures(@PathVariable String username)
	{
		Account user = accountRepository.findByUsername(username);
		
		if (user == null)
		{
			throw new AccountNotFoundException("The specified user could not be found");
		}
		
		return pictureService.getAccountPictures(user);
	}

	@PostMapping(value = "/picture/like/{pictureId}")
	public int likePicture(@PathVariable final String pictureId)
	{
		return pictureService.likePicture(pictureId);
	}

	@PostMapping(value = "/picture/likeRemoval/{pictureId}")
	public int unlikePicture(@PathVariable final String pictureId)
	{
		return pictureService.unlikePicture(pictureId);
	}

	@GetMapping(value = "/picture/likeStatus/{pictureId}")
	public boolean getLikeStatusPicture(@PathVariable final String pictureId)
	{
		return pictureService.getLikeStatus(pictureId);
	}
}
