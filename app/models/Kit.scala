package models

import models.AccessionType.AccessionType
import models.CallerType.CallerType
import models.FileType.FileType
import models.TestType.TestType
import org.joda.time.DateTime
import play.api.libs.json._
import reactivemongo.api.bson._
import reactivemongo.play.json.compat._
import bson2json._

import java.util.UUID

/**
 * @author James R. Kane
 * @version 3.0
 * @since 2022-08-19
 */
object FileType extends Enumeration {
  type FileType = Value

  val VCF   = Value(1)
  val BED   = Value(2)
  val gVCF  = Value(3)
  val BAM   = Value(4)
  val CRAM  = Value(5)
  val StrCSV   = Value(6)
  val mVAR  = Value(7)
  val varComp = Value(10)
  val gType = Value(11)
  val lobystr = Value(12)
  val ystr = Value(13)
  val snpCSV = Value(14)
  val tabBIS = Value(15)
  val tab23nMe = Value(16)
  val tabAnc = Value(17)
  val tabldna = Value(18)
  val csvldna = Value(19)
  val csvmyH = Value(20)
  val zip = Value(21)
}

object CallerType extends Enumeration {
  type CallerType = Value

  val Gatk3     = Value(1, "gatk3")
  val Gatk4     = Value(2, "gatk4")
  val FreeBayes = Value(3, "freebayes")
  val AEngine   = Value(4, "aengine")
  val NA        = Value(5, "n/a")
  val Fgc       = Value(6, "fgc")   // This is really a gatk3 workflow
  val CG        = Value(7, "cg")
}

/**
 * Read and alignment information about a shot-gun sequencing test.
 *
 * @param alignedReads Number of aligned reads
 * @param totalReads   Total number of reads
 * @param readLen      Length of the reads
 * @param insertLen    Estimated insert size for paired-ends
 * @param callable     Estimated callable locations
 * @param poorAlign    Estimated poorly aligned locations
 * @param lowCov       Estimated low coverage locations
 * @param noCov        Estimated no-coverage locations
 */
case class NgsStats(
                     alignedReads: Option[Int],
                     totalReads: Option[Int],
                     readLen: Option[Int],
                     insertLen: Option[Int],
                     callable: Option[Int],
                     poorAlign: Option[Int],
                     lowCov: Option[Int],
                     noCov: Option[Int]
                   )

/**
 * A test data file
 *
 * Using an assumption that if non-chrY contigs are added to a future WGS warehouse, we would be splitting a WGS by contig.
 * This would allow each chromosome to have it's own metrics collected for the ability to make calls, however it would likely
 * make the documents too large for MongoDB.
 *
 * @param _id           Mongo DB ID
 * @param fileType      Type of file
 * @param accession     Accession the file is aligned with
 * @param origName      Original file name
 * @param checkSum      The file checksum
 * @param urlPath       The path to the file in S3
 * @param indexPath     The path to the file index in S3
 * @param ngsStats      Shot-gun sequencing statistics
 * @param caller        The caller used for a call file
 * @param _creationDate The date the file was added
 * @param _updateDate   The date the file was last updated
 */
case class FileData(
                     _id: Option[BSONObjectID],
                     fileType: FileType,
                     accession: AccessionType,
                     origName: Option[String],
                     checkSum: Option[String],
                     urlPath: Option[String],
                     indexPath: Option[String],
                     ngsStats: Option[NgsStats],
                     caller: Option[CallerType],
                     _creationDate: Option[DateTime],
                     _updateDate: Option[DateTime]
                   )

/**
 * Sequencing Data
 *
 * This is a simple container for a specific type of test and the files associated with it
 *
 * @param testType The type of test
 * @param fileData The files that have been provided
 */
case class SequencingData(
                           testType: TestType,
                           fileData: Set[FileData]
                         )

/**
 * Call
 *
 * The specific calls for a location imported kit on a given file
 *
 * @param fileID        The file that provided the call evidence
 * @param allele        The called allele
 * @param depth         The depth of sequencing
 * @param qual          The call quality
 * @param _creationDate The date the call was added
 * @param _updateDate   The last update of the call
 */
case class Call(
                 fileID: Option[BSONObjectID], // YSEQ for now as the capability to add Sanger results without a file
                 allele: String,
                 depth: Option[Int],
                 qual: Option[Double],
                 _creationDate: Option[DateTime],
                 _updateDate: Option[DateTime]
               )

/**
 * CallData
 *
 * @param accession The accession used a reference
 * @param start     The start position of a call
 * @param stop      The stop position of a call
 * @param calls     Calls made at the site
 */
case class CallData(
                     accession: AccessionType,
                     start: Int,
                     stop: Int,
                     calls: Set[Call]
                   )

/**
 * Using a special container for STRs since not all the locations for the sites in use are published, so modeling as
 * CallData is not possible at the time this is being written.
 *
 * @param name         Common name of the STR
 * @param repeats      Primary repetition count
 * @param microRepeats Micro-repeat count
 * @param alleles      The specific allele counted for the DYSxxxX tests
 */
case class StrData(
                    name: String,
                    repeats: Int,
                    microRepeats: Option[Int],
                    alleles: Option[String]
                  )

/**
 * Kit
 *
 * The Kit is a testing kit at a specific lab for a Subject.  The document holds information about the kit,
 * test files, and calls derived from the test files.
 *
 * @param _id           Mongo DB ID
 * @param _creationDate The creation time
 * @param _updateDate   The update time
 * @param subjectID     The ID of the Subject this kit is associated with
 * @param labID         The Lab that performed the testing
 * @param kitName       The Lab's identifier, when available.
 * @param tests         The tests performed and the kit along with the associated files
 * @param calls         The calls extracted from project reports or raw data files
 * @param strs          The STR calls because we can't have nice things
 */
case class Kit(
                _id: Option[UUID],
                _creationDate: Option[DateTime],
                _updateDate: Option[DateTime],
                subjectID: UUID,
                labID: Int,
                kitName: Option[String] = None,
                tests: List[SequencingData],
                calls: List[CallData],
                strs: List[StrData]
              )

// ===================== JSON and BSON Document Binding Boiler Plate =====================

object NgsStats {
  implicit object NgsStatsWrites extends OWrites[NgsStats] {
    override def writes(o: NgsStats): JsObject = Json.obj(
      "alignedReads" -> o.alignedReads,
      "totalReads" -> o.totalReads,
      "readLen" -> o.readLen,
      "insertLen" -> o.insertLen,
      "callable" -> o.callable,
      "poorAlign" -> o.poorAlign,
      "lowCov" -> o.lowCov,
      "noCov" -> o.noCov
    )
  }

  implicit object NgsStatsReads extends Reads[NgsStats] {
    override def reads(json: JsValue): JsResult[NgsStats] = json match {
      case obj: JsObject => try {
        JsSuccess(
          NgsStats(
            (obj \ "alignedReads").asOpt[Int],
            (obj \ "totalReads").asOpt[Int],
            (obj \ "readLen").asOpt[Int],
            (obj \ "insertLen").asOpt[Int],
            (obj \ "callable").asOpt[Int],
            (obj \ "poorAlign").asOpt[Int],
            (obj \ "lowCov").asOpt[Int],
            (obj \ "noCov").asOpt[Int]
          )
        )
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }
      case _ => JsError("expected.jsobject")
    }
  }
}

object FileData {
  implicit object FileDataWrites extends OWrites[FileData] {
    override def writes(o: FileData): JsObject = Json.obj(
      "_id" -> o._id,
      "fileType" -> o.fileType.id,
      "accession" -> o.accession.id,
      "origName" -> o.origName,
      "checkSum" -> o.checkSum,
      "urlPath" -> o.urlPath,
      "indexPath" -> o.indexPath,
      "ngsStats" -> o.ngsStats,
      "caller" -> o.caller.map(_.id),
      "_creationDate" -> o._creationDate.fold(-1L)(_.getMillis),
      "_updateDate" -> o._updateDate.fold(-1L)(_.getMillis),
    )
  }

  implicit object FileDataReads extends Reads[FileData] {
    override def reads(json: JsValue): JsResult[FileData] = json match {
      case obj: JsObject => try {
        JsSuccess(
          FileData(
            (obj \ "_id").asOpt[BSONObjectID],
            FileType.apply((obj \ "fileType").as[Int]),
            AccessionType.apply((obj \ "accession").as[Int]),
            (obj \ "origName").asOpt[String],
            (obj \ "checkSum").asOpt[String],
            (obj \ "urlPath").asOpt[String],
            (obj \ "indexPath").asOpt[String],
            (obj \ "ngsStats").asOpt[NgsStats],
            (obj \ "caller").asOpt[Int].map(CallerType.apply),
            (obj \ "_creationDate").asOpt[Long].map(new DateTime(_)),
            (obj \ "_updateDate").asOpt[Long].map(new DateTime(_))
          )
        )
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }
      case _ => JsError("expected.jsobject")
    }
  }
}

object SequencingData {
  implicit object SequencingDataWrites extends OWrites[SequencingData] {
    override def writes(o: SequencingData): JsObject = Json.obj(
      "testType" -> o.testType.id,
      "fileData" -> o.fileData
    )
  }

  implicit object SequencingDataReads extends Reads[SequencingData] {
    override def reads(json: JsValue): JsResult[SequencingData] = json match {
      case obj: JsObject => try {
        JsSuccess(
          SequencingData(
            TestType.apply((obj \ "testType").as[Int]),
            (obj \ "fileData").as[Set[FileData]]
          )
        )
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }
      case _ => JsError("expected.jsobject")
    }
  }
}

object Call {
  implicit object callWrites extends OWrites[Call] {
    override def writes(o: Call): JsObject = Json.obj(
      "fileID" -> o.fileID,
      "allele" -> o.allele,
      "depth" -> o.depth,
      "qual" -> o.qual,
      "_creationDate" -> o._creationDate.fold(-1L)(_.getMillis),
      "_updateDate" -> o._updateDate.fold(-1L)(_.getMillis),
    )
  }

  implicit object callReads extends Reads[Call] {
    override def reads(json: JsValue): JsResult[Call] = json match {
      case obj: JsObject => try {
        JsSuccess(
          Call(
            (obj \ "fileID").asOpt[BSONObjectID],
            (obj \ "allele").as[String],
            (obj \ "depth").asOpt[Int],
            (obj \ "qual").asOpt[Double],
            (obj \ "_creationDate").asOpt[Long].map(new DateTime(_)),
            (obj \ "_updateDate").asOpt[Long].map(new DateTime(_))
          )
        )
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }
      case _ => JsError("expected.jsobject")
    }
  }
}

object CallData {
  implicit object callDataWrites extends OWrites[CallData] {
    override def writes(o: CallData): JsObject = Json.obj(
      "accession" -> o.accession.id,
      "start" -> o.start,
      "stop" -> o.stop,
      "calls" -> o.calls
    )
  }

  implicit object callDataReads extends Reads[CallData] {
    override def reads(json: JsValue): JsResult[CallData] = json match {
      case obj: JsObject => try {
        JsSuccess(
          CallData(
            AccessionType.apply((obj \ "accession").as[Int]),
            (obj \ "start").as[Int],
            (obj \ "stop").as[Int],
            (obj \ "calls").as[Set[Call]]
          )
        )
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }
      case _ => JsError("expected.jsobject")
    }
  }
}

object StrData {
  implicit object strDataWrites extends OWrites[StrData] {
    override def writes(o: StrData): JsObject = Json.obj(
      "name" -> o.name,
      "repeats" -> o.repeats,
      "microRepeats" -> o.microRepeats,
      "alleles" -> o.alleles
    )
  }

  implicit object strDataReads extends Reads[StrData] {
    override def reads(json: JsValue): JsResult[StrData] = json match {
      case obj: JsObject => try {
        JsSuccess(
          StrData(
            (obj \ "name").as[String],
            (obj \ "repeats").as[Int],
            (obj \ "microRepeats").asOpt[Int],
            (obj \ "alleles").asOpt[String]
          )
        )
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }
      case _ => JsError("expected.jsobject")
    }
  }
}

object Kit {
  implicit object KitWrites extends OWrites[Kit] {
    override def writes(o: Kit): JsObject = Json.obj(
      "_id" -> o._id,
      "_creationDate" -> o._creationDate.fold(-1L)(_.getMillis),
      "_updateDate" -> o._updateDate.fold(-1L)(_.getMillis),
      "subjectID" -> o.subjectID,
      "labID" -> o.labID,
      "kitName" -> o.kitName,
      "tests" -> o.tests,
      "calls" -> o.calls,
      "strs" -> o.strs
    )
  }

  implicit object kitReads extends Reads[Kit] {
    override def reads(json: JsValue): JsResult[Kit] = json match {
      case obj: JsObject => try {
        JsSuccess(
          Kit(
            (obj \ "_id").asOpt[UUID],
            (obj \ "_creationDate").asOpt[Long].map(new DateTime(_)),
            (obj \ "_updateDate").asOpt[Long].map(new DateTime(_)),
            (obj \ "subjectID").as[UUID],
            (obj \ "labId").as[Int],
            (obj \ "kitName").asOpt[String],
            (obj \ "tests").as[List[SequencingData]],
            (obj \ "calls").as[List[CallData]],
            (obj \ "strs").as[List[StrData]]
          )
        )
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }
      case _ => JsError("expected.jsobject")
    }
  }
}