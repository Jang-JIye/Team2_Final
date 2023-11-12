package com.sparta.team2project.profile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.sparta.team2project.commons.entity.UserRoleEnum;
import com.sparta.team2project.posts.dto.TripDateOnlyRequestDto;
import com.sparta.team2project.posts.entity.PostCategory;
import com.sparta.team2project.posts.entity.Posts;
import com.sparta.team2project.s3.CustomMultipartFile;
import com.sparta.team2project.schedules.dto.CreateSchedulesRequestDto;
import com.sparta.team2project.schedules.dto.SchedulesRequestDto;
import com.sparta.team2project.schedules.entity.Schedules;
import com.sparta.team2project.tripdate.entity.TripDate;
import com.sparta.team2project.users.UserRepository;
import com.sparta.team2project.users.Users;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sparta.team2project.schedules.entity.SchedulesCategory.카페;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Testcontainers
public class ProfileImgServiceTest {

    private ProfileRepository profileRepository;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    private ProfileService profileService;

    private static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse("localstack/localstack:latest");

    @Container
    LocalStackContainer localStackContainer = new LocalStackContainer(LOCALSTACK_IMAGE)
            .withServices(S3);

    @Before
    public AmazonS3 setUpS3() {

        AmazonS3 amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(localStackContainer.getEndpointConfiguration(S3))
                .withCredentials(localStackContainer.getDefaultCredentialsProvider())
                .build();
        return amazonS3;
    }

    @BeforeEach
    public void setUp() {
        AmazonS3Client amazonS3 = (AmazonS3Client) setUpS3();
        profileRepository = Mockito.mock(ProfileRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        profileService = new ProfileService(userRepository, passwordEncoder, profileRepository, amazonS3);
    }


    public Users MockUsers(){
        return new Users("test@email.com", "test", "test123!", UserRoleEnum.USER, "image/profileImg.png");
    }

    public SchedulesRequestDto MockSchedulesRequestDto(){
        // Mock the MenuRequestDto
        SchedulesRequestDto schedulesRequestDto = mock(SchedulesRequestDto.class);
        // Set up the behavior of the mock DTO
        when(schedulesRequestDto.getSchedulesCategory()).thenReturn(카페);
        when(schedulesRequestDto.getCosts()).thenReturn(10000);
        when(schedulesRequestDto.getPlaceName()).thenReturn("정동진 카페");
        when(schedulesRequestDto.getContents()).thenReturn("해돋이 카페");
        when(schedulesRequestDto.getTimeSpent()).thenReturn("3시간");
        when(schedulesRequestDto.getReferenceURL()).thenReturn("www.blog.com");
        return schedulesRequestDto;
    }

    public CreateSchedulesRequestDto MockCreateSchedulesRequestDto(){
        CreateSchedulesRequestDto createSchedulesRequestDto = mock(CreateSchedulesRequestDto.class);
        List<SchedulesRequestDto> schedulesRequestDtoList = new ArrayList<>();
        schedulesRequestDtoList.add(MockSchedulesRequestDto());
        when(createSchedulesRequestDto.getSchedulesList()).thenReturn(schedulesRequestDtoList);
        return createSchedulesRequestDto;
    }

    public TripDateOnlyRequestDto MockTripDateOnlyRequestDto(){
        TripDateOnlyRequestDto tripDateOnlyRequestDto = mock(TripDateOnlyRequestDto.class);
        when(tripDateOnlyRequestDto.getChosenDate()).thenReturn(LocalDate.of(2023, 10, 10));
        return tripDateOnlyRequestDto;
    }

    public Posts MockPosts(){
        return new Posts("해돋이 보러간다", "정동진 해돋이", PostCategory.가족, "동해안 해돋이", MockUsers());
    }


    public TripDate MockTripDate(){
        return new TripDate(MockTripDateOnlyRequestDto(), MockPosts());
    }


    public Schedules MockSchedules(){
        return new Schedules(MockTripDate(), MockSchedulesRequestDto());
    }

    public Profile MockProfile(){
        return new Profile(MockUsers());
    }

    public MultipartFile MockFile(int originWidth, int originHeight) throws IOException {
        BufferedImage image = new BufferedImage(originWidth, originHeight, BufferedImage.TYPE_INT_RGB);
        String name = "image";
        String fileName = "test.png";
        String fileFormatName = "png";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write( image, fileFormatName, baos );
        baos.flush();
        byte[] content = baos.toByteArray();

        return new CustomMultipartFile(name, fileName, fileFormatName, content);
    }
