//package com.sparta.team2project.profile;
//
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import com.amazonaws.services.s3.model.S3Object;
//import com.sparta.team2project.commons.entity.UserRoleEnum;
//import com.sparta.team2project.s3.CustomMultipartFile;
//import com.sparta.team2project.users.UserRepository;
//import com.sparta.team2project.users.Users;
//import org.junit.Before;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.multipart.MultipartFile;
//import org.testcontainers.containers.localstack.LocalStackContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.utility.DockerImageName;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;
//
//@Testcontainers
//public class ProfileImgServiceTest {
//
//    private ProfileRepository profileRepository;
//
//    private UserRepository userRepository;
//
//    private PasswordEncoder passwordEncoder;
//    private ProfileService profileService;
//
//    private static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse("localstack/localstack:latest");
//
//    @Container
//    LocalStackContainer localStackContainer = new LocalStackContainer(LOCALSTACK_IMAGE)
//            .withServices(S3);
//
//    @Before
//    public AmazonS3 setUpS3() {
//
//        AmazonS3 amazonS3 = AmazonS3ClientBuilder
//                .standard()
//                .withEndpointConfiguration(localStackContainer.getEndpointConfiguration(S3))
//                .withCredentials(localStackContainer.getDefaultCredentialsProvider())
//                .build();
//        return amazonS3;
//    }
//
//    @BeforeEach
//    public void setUp() {
//        AmazonS3Client amazonS3 = (AmazonS3Client) setUpS3();
//        profileRepository = Mockito.mock(ProfileRepository.class);
//        userRepository = Mockito.mock(UserRepository.class);
//        passwordEncoder = Mockito.mock(PasswordEncoder.class);
//        profileService = new ProfileService(userRepository, passwordEncoder, profileRepository, amazonS3);
//    }
//
//
//    public Users MockUsers(){
//        return new Users("test@email.com", "test", "test123!", UserRoleEnum.USER, "image/profileImg.png");
//    }
//
//    public Profile MockProfile(){
//        return new Profile(MockUsers());
//    }
//
//    public MultipartFile MockFile(int originWidth, int originHeight) throws IOException {
//        BufferedImage image = new BufferedImage(originWidth, originHeight, BufferedImage.TYPE_INT_RGB);
//        String name = "image";
//        String fileName = "test.png";
//        String fileFormatName = "png";
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write( image, fileFormatName, baos );
//        baos.flush();
//        byte[] content = baos.toByteArray();
//
//        return new CustomMultipartFile(name, fileName, fileFormatName, content);
//    }
//
//    @Test
//    public void testUpdateAndReadProfileImg() throws Exception {
//        AmazonS3 amazonS3 = setUpS3();
//        Users users = MockUsers(); // Create a mock User
//        Profile profile = MockProfile();
//
//        System.out.println("업데이트 전 이미지 URL: " + profile.getUsers().getProfileImg());
//        MultipartFile mockFile = MockFile(640, 480);
//
//        // Mocking the behavior
//        when(profileRepository.save(profile)).thenReturn(profile);
//        when(profileRepository.findByUsers_Email(users.getEmail())).thenReturn(Optional.of(profile));
//
//        // 이미지 사이즈 조정 테스트
//        MultipartFile resizedImage = profileService.resizer(mockFile, 96, 96);
//
//        BufferedImage resizedImageRead = ImageIO.read(resizedImage.getInputStream());
//
//        // 리사이즈된 이미지 업데이트 후 프로필 Repository에 저장
//        String profileImgURL = "image/" + resizedImage.getOriginalFilename();
//        profile.getUsers().updateProfileImg(profileImgURL);
//        profileRepository.save(profile);
//
//        Optional<Profile> profileUpdated = profileRepository.findByUsers_Email(users.getEmail());
//
//        // 프로필의 업데이트된 이미지 URL반환
//        String profileImgURLUpdated = profileUpdated.get().getUsers().getProfileImg();
//        System.out.println("업데이트 후 이미지 URL: " + profileImgURLUpdated);
//
//        // 변환된 이미지 사이즈 확인
//        System.out.println("변환된 width: " + resizedImageRead.getWidth());
//        System.out.println("변환된 height: " + resizedImageRead.getHeight());
//
//        assertEquals(resizedImageRead.getWidth(), 96);
//        assertEquals(resizedImageRead.getHeight(), 96);
//
//        // S3에 이미지 업로드 및 확인
//        String bucketName = "foo";
//        amazonS3.createBucket(bucketName);
//        System.out.println(bucketName +" 버킷 생성");
//
//        String key = resizedImage.getOriginalFilename();
//        String content = "테스트 사진";
//        amazonS3.putObject(bucketName, key, content);
//        System.out.println("파일을 업로드하였습니다. 파일 이름=" + key +", 파일 내용=" + content);
//
//        S3Object object = amazonS3.getObject(bucketName, key);
//        System.out.println("파일을 가져왔습니다. 파일 이름=" + object.getKey());
//        assertEquals(key, object.getKey());
//    }
//
//    @Test
//    public void testDefaultProfileImg() {
//        Users users = MockUsers(); // Create a mock User
//        Profile profile = MockProfile();
//
//        // 최초 사진 URL확인
//        String profileImgURL = profile.getUsers().getProfileImg();
//        System.out.println("업데이트 전 프로필 이미지 URL: " + profileImgURL);
//
//        // Mocking the behavior
//        when(profileRepository.save(profile)).thenReturn(profile);
//        when(profileRepository.findByUsers_Email(users.getEmail())).thenReturn(Optional.of(profile));
//
//        // 기본 이미지 URL
//        String defaultPictureURL = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fb0SLv8%2FbtsyLoUxvAs%2FSKsGiOc7TzkebNvH4ZQE9K%2Fimg.png";
//
//        profile.getUsers().updateProfileImg(defaultPictureURL);
//        profileRepository.save(profile);
//
//        Optional<Profile> profileUpdated = profileRepository.findByUsers_Email(profile.getUsers().getEmail());
//        // Test the method
//        String profileImgURLUpdated = profileUpdated.get().getUsers().getProfileImg();
//
//        // 업데이트된 프로필 이미지 확인
//        System.out.println("업데이트 후 프로필 이미지 URL: " + profileImgURLUpdated);
//
//        // Assertions
//        // Add relevant assertions here based on the expected behavior
//        assertEquals(defaultPictureURL, profileImgURLUpdated); // Replace "DefaultURL" with the expected URL
//    }
//}