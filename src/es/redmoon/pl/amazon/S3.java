package es.redmoon.pl.amazon;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import es.redmoon.utils.LOBUtils;
import es.redmoon.utils.XML;

import oracle.sql.BLOB;

/**
 * Class for work with amazon S3.
 * @version 0.1
 */
public class S3 {

	/**
	 * Create a bucket.
	 * @param accessKey User's amazon access key.
	 * @param secretKey User's amazon secret key.
	 * @param bucketName Bucket's name.
	 * @return true if bucked has been created or null in case of error.
	 */
	public static boolean createBucket (String accessKey, String secretKey, String bucketName) {
		
		boolean isCreated = false;
		
		// Setting credentials
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
				
		// Creating client
		AmazonS3 s3 = new AmazonS3Client(credentials);
		
		try {
			s3.createBucket(bucketName);
			isCreated = true;
			
		} catch (AmazonServiceException e) {
			Logger.getLogger(S3.class.getName()).log(Level.SEVERE, "Error creating bucket.", e);
			isCreated = false;
		} catch (AmazonClientException e) {
			Logger.getLogger(S3.class.getName()).log(Level.SEVERE, "Error creating bucket.", e);
			isCreated = false;
		}
		
		return isCreated;
	}
	
	/**
	 * Delete a bucket.
	 * @param accessKey User's amazon access key.
	 * @param secretKey User's amazon secret key.
	 * @param bucketName Bucket's name.
	 * @return true if bucket has been deleted or false in case of error.
	 */
	public static boolean deleteBucket (String accessKey, String secretKey, String bucketName) {
		boolean isDeleted = false;
		
		// Setting credentials
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
				
		// Creating client
		AmazonS3 s3 = new AmazonS3Client(credentials);
		
		try {
			s3.deleteBucket(bucketName);
			isDeleted = true;
			
		} catch (AmazonServiceException e) {
			Logger.getLogger(S3.class.getName()).log(Level.SEVERE, "Error deleting bucket.", e);
			isDeleted = false;
		} catch (AmazonClientException e) {
			Logger.getLogger(S3.class.getName()).log(Level.SEVERE, "Error deleting bucket.", e);
			isDeleted = false;
		}
		
		return isDeleted;
	}
	
	/**
	 * Put a object in a bucket.
	 * @param accessKey User's amazon access key.
	 * @param secretKey User's amazon secret key.
	 * @param bucketName Bucket's name.
	 * @param object The object to put in bucket.
	 * @param key The key/name under which to store the new object.
	 * @return true if object has been putted in the bucket or false in case of error.
	 */
	public static boolean putObject (String accessKey, String secretKey, String bucketName, BLOB object, String key) {
		
		boolean isPutted = false;

		// Setting credentials
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

		// Creating client
		AmazonS3 s3 = new AmazonS3Client(credentials);

		S3Object s3Object = new S3Object();		

		// Setting object mime type
		//myS3Object.getObjectMetadata().setContentType(mimeType);

		try {
			
			// Setting object length
			s3Object.getObjectMetadata().setContentLength(object.length());

			// Putting object in bucket
			s3.putObject(new PutObjectRequest(bucketName, key, object.getBinaryStream(), s3Object.getObjectMetadata()));

			isPutted = true;
		} catch (AmazonServiceException e) {
			Logger.getLogger(S3.class.getName()).log(Level.SEVERE, "Error putting object.", e);
			isPutted = false;
		} catch (AmazonClientException e) {
			Logger.getLogger(S3.class.getName()).log(Level.SEVERE, "Error putting object.", e);
			isPutted = false;
		} catch (SQLException e) {
			Logger.getLogger(S3.class.getName()).log(Level.SEVERE, "Error with database putting object.", e);
			isPutted = false;
		}

		return isPutted;
	}
	
	/**
	 * Get a object from a bucket.
	 * @param accessKey User's amazon access key.
	 * @param secretKey User's amazon secret key.
	 * @param bucketName Bucket's name.
	 * @param key The key/name under which the desired object is stored. 
	 * @return BLOB containing the object or null in case of error.
	 */
	public static BLOB getObject (String accessKey, String secretKey, String bucketName, String key) {
		
		BLOB object = null;
		InputStream is = null;
		
		// Setting credentials
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

		// Creating client
		AmazonS3 s3 = new AmazonS3Client(credentials);

		try {
			S3Object s3Object = s3.getObject(bucketName, key);
			
			is = s3Object.getObjectContent();			
			
			object = LOBUtils.InputStreamToBLOB(is);

		} catch (AmazonServiceException e) {
			Logger.getLogger(S3.class.getName()).log(Level.SEVERE, "Error getting object.", e);
			object = null;
		} catch (AmazonClientException e) {
			Logger.getLogger(S3.class.getName()).log(Level.SEVERE, "Error getting object.", e);
			object = null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Logger.getLogger(XML.class.getName()).log(Level.SEVERE, "Error freeing resources.", e);
				}
			}
		}
		
		return object;
	}
	
	/**
	 * Delete a object from a bucket.
	 * @param accessKey User's amazon access key.
	 * @param secretKey User's amazon secret key.
	 * @param bucketName Bucket's name.
	 * @param key The key/name under which the desired object is stored. 
	 * @return true if the object has been deleted or false in case of error.
	 */
	public static boolean deleteObject (String accessKey, String secretKey, String bucketName, String key) {
		
		boolean isDeleted = false;

		// Setting credentials
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

		// Creating client
		AmazonS3 s3 = new AmazonS3Client(credentials);

		try {
			
			s3.deleteObject(bucketName, key);
			
			isDeleted = true;

		} catch (AmazonServiceException e) {
			Logger.getLogger(S3.class.getName()).log(Level.SEVERE, "Error getting object.", e);
			isDeleted = false;
		} catch (AmazonClientException e) {
			Logger.getLogger(S3.class.getName()).log(Level.SEVERE, "Error getting object.", e);
			isDeleted = false;
		}
		
		return isDeleted;
	}
	
}
