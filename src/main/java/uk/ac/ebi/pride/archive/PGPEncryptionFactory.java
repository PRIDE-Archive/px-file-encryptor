package uk.ac.ebi.pride.archive;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import uk.ac.ebi.pride.archive.cryptography.Cryptography;
import uk.ac.ebi.pride.archive.cryptography.pgp.PGPCryptography;
import uk.ac.ebi.pride.archive.stream.pipeline.DefaultStream;
import uk.ac.ebi.pride.archive.stream.pipeline.PipelineStream;
import uk.ac.ebi.pride.archive.utils.FileUtils;
import uk.ac.ebi.pride.archive.utils.Hash;

import java.io.*;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;

import static uk.ac.ebi.pride.archive.constant.FileExtensionType.GPG;
import static uk.ac.ebi.pride.archive.constant.FileExtensionType.MD5;
import static uk.ac.ebi.pride.archive.utils.FileUtils.writeToFile;

public class PGPEncryptionFactory {

    public static final Integer BUFFER_SIZE = 2048;

    public static void encrypt(File inputFile) throws Exception{

        final Resource resource = new ClassPathResource("publicKey.asc");
        if (!resource.exists()) {
            throw new RuntimeException("Public key file ".concat("publicKey.asc").concat(" not found"));
        }
        InputStream inputStream = resource.getInputStream();
        Cryptography cryptography = new PGPCryptography(inputStream, BUFFER_SIZE);
        final MessageDigest inputStreamMessageDigest = Hash.getMD5();
        final MessageDigest outputStreamMessageDigest = Hash.getMD5();

        final File outputFileMD5 = FileUtils.newEmptyPath().resolve(inputFile.getParent()).resolve(inputFile.getName().
                concat(MD5.getFileExtension())).toFile();
        final File outputFileGPG = FileUtils.newEmptyPath().resolve(inputFile.getParent()).resolve(inputFile.getName().
                concat(GPG.getFileExtension())).toFile();
        final File outputFileGPGMD5 = FileUtils.newEmptyPath().resolve(inputFile.getParent()).resolve(inputFile.getName().
                concat(GPG.getFileExtension().concat(MD5.getFileExtension()))).toFile();

        final DigestInputStream digestInputStream = new DigestInputStream(new FileInputStream(inputFile), inputStreamMessageDigest);//Will be closed in PipelineStream
        long bytesRead;

        try (final DigestOutputStream digestOutputStream = new DigestOutputStream(new FileOutputStream(outputFileGPG), outputStreamMessageDigest)) {
            final OutputStream pgpEncryptedOutputStream = cryptography.encrypt(digestOutputStream);//Will be closed in PipelineStream
            try (final PipelineStream pipelineStream = new DefaultStream(digestInputStream, pgpEncryptedOutputStream, BUFFER_SIZE)) {
                bytesRead = pipelineStream.execute();
            }
        }
        writeToFile(outputFileMD5, Hash.normalize(inputStreamMessageDigest));
        writeToFile(outputFileGPGMD5, Hash.normalize(outputStreamMessageDigest));
    }
}
