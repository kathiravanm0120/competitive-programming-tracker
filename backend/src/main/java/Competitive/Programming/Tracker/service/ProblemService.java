package Competitive.Programming.Tracker.service;

import Competitive.Programming.Tracker.dto.ProblemRequest;
import Competitive.Programming.Tracker.dto.ProblemResponse;
import Competitive.Programming.Tracker.entity.Problem;
import Competitive.Programming.Tracker.entity.User;
import Competitive.Programming.Tracker.repository.ProblemRepository;
import Competitive.Programming.Tracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;

    public ProblemService(
            ProblemRepository problemRepository,
            UserRepository userRepository) {

        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
    }

    public List<ProblemResponse> getUserProblems(String username) {

        User user = getUser(username);

        return problemRepository.findByUser(user)
                .stream()
                .map(ProblemResponse::new)
                .toList();
    }

    public ProblemResponse createProblem(
            ProblemRequest request,
            String username) {

        User user = getUser(username);

        Problem problem = new Problem();

        problem.setTitle(request.getTitle());
        problem.setPlatform(request.getPlatform());
        problem.setUrl(request.getUrl());
        problem.setDifficulty(request.getDifficulty());
        problem.setTopic(request.getTopic());
        problem.setStatus(request.getStatus());
        problem.setNotes(request.getNotes());
        problem.setFavorite(request.isFavorite());

        problem.setUser(user);

        Problem savedProblem = problemRepository.save(problem);

        return new ProblemResponse(savedProblem);
    }

    public ProblemResponse updateProblem(
            Long id,
            ProblemRequest request,
            String username) {

        User user = getUser(username);

        Problem existingProblem = problemRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        if (!existingProblem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You cannot modify this problem");
        }

        existingProblem.setTitle(request.getTitle());
        existingProblem.setPlatform(request.getPlatform());
        existingProblem.setUrl(request.getUrl());
        existingProblem.setDifficulty(request.getDifficulty());
        existingProblem.setTopic(request.getTopic());
        existingProblem.setStatus(request.getStatus());
        existingProblem.setNotes(request.getNotes());
        existingProblem.setFavorite(request.isFavorite());

        Problem updatedProblem = problemRepository.save(existingProblem);

        return new ProblemResponse(updatedProblem);
    }

    public void deleteProblem(
            Long id,
            String username) {

        User user = getUser(username);

        Problem problem = problemRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        if (!problem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You cannot delete this problem");
        }

        problemRepository.delete(problem);
    }

    private User getUser(String username) {

        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}