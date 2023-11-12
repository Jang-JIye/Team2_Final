//package com.sparta.team2project.pictures;
//
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import com.amazonaws.services.s3.model.AmazonS3Exception;
//import com.amazonaws.services.s3.model.S3Object;
//import com.sparta.team2project.commons.entity.UserRoleEnum;
//import com.sparta.team2project.pictures.entity.Pictures;
//import com.sparta.team2project.pictures.repository.PicturesRepository;
//import com.sparta.team2project.posts.dto.TripDateOnlyRequestDto;
//import com.sparta.team2project.posts.entity.PostCategory;
//import com.sparta.team2project.posts.entity.Posts;
//import com.sparta.team2project.schedules.dto.CreateSchedulesRequestDto;
//import com.sparta.team2project.schedules.dto.SchedulesRequestDto;
//import com.sparta.team2project.schedules.entity.Schedules;
//import com.sparta.team2project.tripdate.entity.TripDate;
//import com.sparta.team2project.users.Users;
//import org.junit.Before;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.testcontainers.containers.localstack.LocalStackContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.utility.DockerImageName;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.sparta.team2project.schedules.entity.SchedulesCategory.카페;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;
//
//// 주의: PicturesService에서는 실제 AWS S3를 활용하므로, 해당 테스트 코드는 직접적인 서비스 테스트코드가 아닌
//// LocalStack상에서 에뮬레이션된 S3를 활용하며, 서비스 코드내 구현된 Repository를 활용한 로직을 테스트하는 방식으로 구현되었습니다.
//@Testcontainers
//public class PicturesServiceTest {
//    private static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse("localstack/localstack:latest");
//
//    @Container
//    LocalStackContainer localStackContainer = new LocalStackContainer(LOCALSTACK_IMAGE)
//            .withServices(S3);
//
//    private PicturesRepository picturesRepository;
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
//        picturesRepository = Mockito.mock(PicturesRepository.class);
//    }
//
//    public Users MockUsers(){
//        return new Users("test@email.com", "test", "test123!", UserRoleEnum.USER, "image/profileImg.png");
//    }
//
//    public SchedulesRequestDto MockSchedulesRequestDto(){
//        // Mock the MenuRequestDto
//        SchedulesRequestDto schedulesRequestDto = mock(SchedulesRequestDto.class);
//        // Set up the behavior of the mock DTO
//        when(schedulesRequestDto.getSchedulesCategory()).thenReturn(카페);
//        when(schedulesRequestDto.getCosts()).thenReturn(10000);
//        when(schedulesRequestDto.getPlaceName()).thenReturn("정동진 카페");
//        when(schedulesRequestDto.getContents()).thenReturn("해돋이 카페");
//        when(schedulesRequestDto.getTimeSpent()).thenReturn("3시간");
//        when(schedulesRequestDto.getReferenceURL()).thenReturn("www.blog.com");
//        return schedulesRequestDto;
//    }
//
//    public CreateSchedulesRequestDto MockCreateSchedulesRequestDto(){
//        CreateSchedulesRequestDto createSchedulesRequestDto = mock(CreateSchedulesRequestDto.class);
//        List<SchedulesRequestDto> schedulesRequestDtoList = new ArrayList<>();
//        schedulesRequestDtoList.add(MockSchedulesRequestDto());
//        when(createSchedulesRequestDto.getSchedulesList()).thenReturn(schedulesRequestDtoList);
//        return createSchedulesRequestDto;
//    }
//
//    public TripDateOnlyRequestDto MockTripDateOnlyRequestDto(){
//        TripDateOnlyRequestDto tripDateOnlyRequestDto = mock(TripDateOnlyRequestDto.class);
//        when(tripDateOnlyRequestDto.getChosenDate()).thenReturn(LocalDate.of(2023, 10, 10));
//        return tripDateOnlyRequestDto;
//    }
//
//    public Posts MockPosts(){
//        return new Posts("해돋이 보러간다", "정동진 해돋이", PostCategory.가족, "동해안 해돋이", MockUsers());
//    }
//
//
//    public TripDate MockTripDate(){
//        return new TripDate(MockTripDateOnlyRequestDto(), MockPosts());
//    }
//
//
//    public Schedules MockSchedules(){
//        return new Schedules(MockTripDate(), MockSchedulesRequestDto());
//    }
//
//    public Pictures MockPictures(){
//        return new Pictures(MockSchedules(), "image/test.png", "test.png", "image/png", 100000L);
//    }
//
//    public Pictures MockPicturesTwo(){
//        return new Pictures(MockSchedules(), "image/test2.jpg", "test2.jpg", "image/jpg", 200000L);
//    }
//
//
//    @Test
//    void testUploadAndGetPictures(){
//        AmazonS3 amazonS3 = setUpS3();
//        Schedules schedules = MockSchedules();
//        List<Pictures> picturesList = schedules.getPicturesList();
//        Pictures pictures = MockPictures();
//        for(int i = 0; i < 3; i++){
//            picturesList.add(pictures);
//        }
//
//        // 입력한 사진 파일이 등록되는 URL값 출력
//        when(picturesRepository.saveAll(picturesList)).thenReturn(picturesList);
//        when(picturesRepository.findAllBySchedules(schedules)).thenReturn(schedules.getPicturesList());
//        System.out.println("등록할 사진 파일 링크");
//        for(Pictures picture: picturesList){
//            System.out.println("사진 파일 링크: " + picture.getPicturesURL());
//        }
//
//        // 등록 가능한 최대 사진 수 확인
//        System.out.println("등록가능한 최대 사진 수: " + picturesList.size());
//        assertTrue(picturesList.size() <= 3);
//
//        // Repository등록 사진 정보 출력
//        List<Pictures> picturesSavedRepository = picturesRepository.saveAll(picturesList);
//
//        for(Pictures picture: picturesSavedRepository){
//            System.out.println("저장 사진 파일 링크: " + picture.getPicturesURL());
//        }
//
//        System.out.println("Repository 저장한 최대 사진 수: " + picturesSavedRepository.size());
//        assertTrue(picturesSavedRepository.size() <= 3);
//        assertEquals(picturesSavedRepository.get(0).getPicturesURL(), picturesList.get(0).getPicturesURL());
//
//        // Repository추출 사진 정보 출력
//        List<Pictures> picturesFromRepository = picturesRepository.findAllBySchedules(schedules);
//
//        for(Pictures picture: picturesFromRepository){
//            System.out.println("추출 사진 파일 링크: " + picture.getPicturesURL());
//        }
//
//        System.out.println("Repository 추출한 최대 사진 수: " + picturesFromRepository.size());
//        assertTrue(picturesFromRepository.size() <= 3);
//        assertEquals(picturesFromRepository.get(0).getPicturesURL(), picturesSavedRepository.get(0).getPicturesURL());
//
//
//        // S3 사진 등록 기능 확인
//        String bucketName = "foo";
//        amazonS3.createBucket(bucketName);
//        System.out.println(bucketName +" 버킷 생성");
//
//        String key = picturesFromRepository.get(0).getPicturesName();
//        String content = "테스트 사진";
//        amazonS3.putObject(bucketName, key, content);
//        System.out.println("파일을 업로드하였습니다. 파일 이름=" + key +", 파일 내용=" + content);
//
//        S3Object object = amazonS3.getObject(bucketName, key);
//        System.out.println("파일을 가져왔습니다. 파일 이름=" + object.getKey());
//        assertEquals(key, object.getKey());
//
//    }
//
//    @Test
//    void testUpdatePictures(){
//        AmazonS3 amazonS3 = setUpS3();
//        Pictures pictures = MockPictures();
//        Pictures picturesTwo = MockPicturesTwo();
//        Schedules schedules = MockSchedules();
//
//        List<Pictures> picturesList = schedules.getPicturesList();
//
//        when(picturesRepository.saveAll(picturesList)).thenReturn(picturesList);
//        when(picturesRepository.findAllBySchedules(schedules)).thenReturn(schedules.getPicturesList());
//
//        // 첫번 째 사진 저장
//        picturesList.add(pictures);
//
//        // 첫번째 사진 저장 결과 출력
//        List<Pictures> picturesListSavedRepository = picturesRepository.saveAll(picturesList);
//        Pictures picturesSavedRepository = picturesListSavedRepository.get(0);
//        Pictures picturesFromRepository = picturesRepository.findAllBySchedules(schedules).get(0);
//
//        assertEquals(picturesSavedRepository.getPicturesURL(), picturesFromRepository.getPicturesURL());
//        System.out.println("최초 저장된 사진 URL: " + picturesSavedRepository.getPicturesURL());
//        System.out.println("추출된 사진 URL: " + picturesFromRepository.getPicturesURL());
//
//        // S3 첫 번째 사진 등록 기능 확인
//        String bucketName = "foo";
//        amazonS3.createBucket(bucketName);
//        System.out.println(bucketName +" 버킷 생성");
//
//        String key = picturesFromRepository.getPicturesName();
//        String content = "테스트 사진1";
//        amazonS3.putObject(bucketName, key, content);
//        System.out.println("파일을 업로드하였습니다. 파일 이름=" + key +", 파일 내용=" + content);
//
//        S3Object object = amazonS3.getObject(bucketName, key);
//        System.out.println("파일을 가져왔습니다. 파일 이름=" + object.getKey());
//        assertEquals(key, object.getKey());
//
//        // 두 번째 사진으로 대체 후 결과 출력
//        picturesList.set(0, picturesTwo);
//
//        Pictures picturesTwoSavedRepository = picturesRepository.saveAll(picturesList).get(0);
//        Pictures picturesTwoFromRepository = picturesRepository.findAllBySchedules(schedules).get(0);
//
//        assertEquals(picturesTwoSavedRepository.getPicturesURL(), picturesTwoFromRepository.getPicturesURL());
//        System.out.println("최초 저장된 사진 URL: " + picturesTwoSavedRepository.getPicturesURL());
//        System.out.println("추출된 사진 URL: " + picturesTwoFromRepository.getPicturesURL());
//
//        // S3 두 번째 사진 등록 기능 확인
//
//        String keyTwo = picturesTwoFromRepository.getPicturesName();
//        String contentTwo = "테스트 사진2";
//        amazonS3.putObject(bucketName, keyTwo, contentTwo);
//        System.out.println("파일을 업로드하였습니다. 파일 이름=" + keyTwo +", 파일 내용=" + contentTwo);
//
//        S3Object objectTwo = amazonS3.getObject(bucketName, keyTwo);
//        System.out.println("파일을 가져왔습니다. 파일 이름=" + objectTwo.getKey());
//        assertEquals(keyTwo, objectTwo.getKey());
//
//    }
//
//    @Test
//    void testDeletePictures() {
//        // Mock data and behaviors
//        AmazonS3 amazonS3 = setUpS3();
//        Pictures pictures = MockPictures();
//        Schedules schedules = MockSchedules();
//        List<Pictures> picturesList = schedules.getPicturesList();
//        picturesList.add(pictures);
//
//        when(picturesRepository.findAllBySchedules(schedules)).thenReturn(schedules.getPicturesList());
//        when(picturesRepository.saveAll(picturesList)).thenReturn(picturesList);
//
//        List<Pictures> pictureSavedRepository = picturesRepository.saveAll(picturesList);
//
//        for(Pictures picture: pictureSavedRepository){
//            System.out.println("저장된 사진 이름: " + picture.getPicturesName());
//        }
//
//        // S3 사진 등록 기능 확인
//        String bucketName = "foo";
//        amazonS3.createBucket(bucketName);
//        System.out.println(bucketName +" 버킷 생성");
//
//        String key = pictureSavedRepository.get(0).getPicturesName();
//        String content = "테스트 사진1";
//        amazonS3.putObject(bucketName, key, content);
//        System.out.println("파일을 업로드하였습니다. 파일 이름=" + key +", 파일 내용=" + content);
//
//        S3Object object = amazonS3.getObject(bucketName, key);
//        System.out.println("파일을 가져왔습니다. 파일 이름=" + object.getKey());
//        assertEquals(key, object.getKey());
//
//        // S3 사진 삭제 기능 확인
//        // 사진 삭제 성공시 파일 삭제 메시지 출력, 아닐시 Exception 출력
//        Exception ex = null;
//        try{
//            amazonS3.deleteObject(bucketName, key);
//        } catch (AmazonS3Exception e){
//            ex = e;
//        }
//
//        assertNull(ex);
//        System.out.println("파일을 삭제하였습니다.");
//
//        // Repository에서 파일 정보 삭제
//
//        picturesList.clear();
//
//        picturesRepository.deleteAll(picturesList);
//
//        if( picturesRepository.findAllBySchedules(schedules).isEmpty()){
//            System.out.println("사진이 모두 삭제되었습니다. ");
//        }
//
//        assertTrue(picturesRepository.findAllBySchedules(schedules).isEmpty());
//
//    }
//}