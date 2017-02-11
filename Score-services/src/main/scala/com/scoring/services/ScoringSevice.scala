package com.scoring.services

import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status
import javax.ws.rs.core.MediaType
import com.scoring.management.RuleManagment
import com.scoring.dto.ErrorResponseDto
import com.scoring.dto.ScoreRequestDto

@Path("/scoreService")
@Produces(Array(MediaType.APPLICATION_JSON))
class ScoringSevice(management: RuleManagment) {
  
  @Path("")
  @GET
  @Produces(Array(MediaType.TEXT_PLAIN))
  def apiDoc(): Response = {
    val manual = """Score-Service
      |
      | User guide -
      |
      |  - To calculate the score send a request with two parameters hoteId & countryId on /scoreService/score
      |    ex. /scoreService/score?hotelId=<hoteId>&countryId=<countryId>
      |
      |  - To see all existing rules send a with No parameters request on /scoreService/ruleDump
      |    ex. /scoringService/ruleDump
      |
      |  - To switch Toggle a rule send a request with two parameters ruleId & enable on /scoreService/enableRule
      |    ex. /scoreService/enableRule?ruleId=<ruleId>&enable=<true/false>
      |
      |  - To update the score of a rule send a request with two parameters ruleId & updatedScore on /scoreService/ruleScore
      |    ex. /scoreService/ruleScore?ruleId=<ruleId>&updatedScore=<newScore>""" 
    Response.ok(manual).build()
  }

  @Path("/score")
  @GET
  def getScore(@QueryParam("hotelId") hotelId: Int, @QueryParam("countryId") countryId: Int): Response = {
    (hotelId, countryId) match {
      case (0, 0)     => Response.ok(ErrorResponseDto(Status.NO_CONTENT.getStatusCode(), "hotelId and countryId must not be empty")).build()
      case (0, _)     => Response.ok(ErrorResponseDto(Status.PARTIAL_CONTENT.getStatusCode(), "hotelId must not be empty")).build()
      case (_, 0)     => Response.ok(ErrorResponseDto(Status.PARTIAL_CONTENT.getStatusCode(), "countryId must not be empty")).build()
      case (hid, cid) => Response.ok(management.getFinalScore(ScoreRequestDto(hid, cid))).build()
    }
  }

  @Path("/enableRule")
  @GET //  @PUT
  def enableRule(@QueryParam("ruleId") ruleId: String, @QueryParam("enable") flag: Boolean): Response = {
    ruleId match {
      case "0" => Response.ok(ErrorResponseDto(Status.PARTIAL_CONTENT.getStatusCode(), "ruleId must not be empty")).build()
      case id if id != "0" => {
        management.enableRule(id, flag) match {
          case None       => Response.ok(ErrorResponseDto(Status.NOT_FOUND.getStatusCode(), "Rule does not exist")).build()
          case Some(rule) => Response.ok(rule).build()
        }
      }
    }
  }

  @Path("/ruleScore")
  @GET //  @PUT
  def updateRuleScore(@QueryParam("ruleId") ruleId: String, @QueryParam("updatedScore") updatedScore: Int): Response = {
    ruleId match {
      case "0" => Response.ok(ErrorResponseDto(Status.PARTIAL_CONTENT.getStatusCode(), "ruleId must not be empty")).build()
      case id if id != "0" => {
        management.updateRuleScore(ruleId, updatedScore) match {
          case None       => Response.ok(ErrorResponseDto(Status.NOT_FOUND.getStatusCode(), "Rule does not exist")).build()
          case Some(rule) => Response.ok(rule).build()
        }
      }
    }

  }

  @Path("/ruleDump")
  @GET
  def ruleDump(): Response = {
    Response.ok(management.ruleDump()) /*.entity("Exisiting Rules in the system-")*/ .build()
  }
}